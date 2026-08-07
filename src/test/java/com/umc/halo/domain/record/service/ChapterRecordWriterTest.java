package com.umc.halo.domain.record.service;

import com.umc.halo.domain.content.chapter.entity.Chapter;
import com.umc.halo.domain.content.storybook.entity.Storybook;
import com.umc.halo.domain.image.event.ImageFinalizeRequestedEvent;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.record.dto.RecordReqDTO;
import com.umc.halo.domain.record.entity.MemberChapter;
import com.umc.halo.domain.record.entity.MemberStorybook;
import com.umc.halo.domain.record.enums.Status;
import com.umc.halo.domain.record.exception.RecordException;
import com.umc.halo.domain.record.exception.code.RecordErrorCode;
import com.umc.halo.domain.record.repository.MemberChapterAnswerRepository;
import com.umc.halo.domain.record.repository.MemberChapterRepository;
import com.umc.halo.domain.record.repository.MemberStorybookRepository;
import com.umc.halo.domain.content.chapter.repository.ChapterQuestionRepository;
import com.umc.halo.domain.content.chapter.repository.ChapterRepository;
import com.umc.halo.domain.content.chapter.repository.SceneCardRepository;
import com.umc.halo.global.ai.event.ChapterCompletedEvent;
import com.umc.halo.global.enums.Emotion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * persist()는 @Transactional 안에서 DB 쓰기를 담당
 * pendingImageKey가 있을 때만 이미지 확정 이벤트가 발행되는지, 저장 충돌 시 예외로 변환되는지 검증
 */
@ExtendWith(MockitoExtension.class)
class ChapterRecordWriterTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ChapterRepository chapterRepository;
    @Mock
    private MemberChapterRepository memberChapterRepository;
    @Mock
    private SceneCardRepository sceneCardRepository;
    @Mock
    private MemberStorybookRepository memberStorybookRepository;
    @Mock
    private MemberChapterAnswerRepository memberChapterAnswerRepository;
    @Mock
    private ChapterQuestionRepository chapterQuestionRepository;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private ChapterRecordWriter chapterRecordWriter;

    private final Member member = Member.builder().id(1L).build();
    private final Storybook storybook = Storybook.builder().id(100L).title("storybook").build();
    private final Chapter chapter = Chapter.builder().id(10L).storybook(storybook).chapterOrder(5).title("chapter").build();

    private void stubCommonLookups() {
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(chapterRepository.findById(10L)).willReturn(Optional.of(chapter));
        given(memberStorybookRepository.findByStorybookAndMember(storybook, member))
                .willReturn(Optional.of(MemberStorybook.builder().id(1000L).build()));
    }

    @Test
    void pendingImageKey가_있으면_이미지_확정_이벤트를_발행한다() {
        stubCommonLookups();
        RecordReqDTO.WriteChapterRecord dto =
                new RecordReqDTO.WriteChapterRecord(10L, null, null, null, null, null, Status.DRAFT);
        ValidatedChapterRecord validated =
                new ValidatedChapterRecord(null, null, "pending/images/1/a.png", "pending/images/1/a.png", "images/1/a.png");

        chapterRecordWriter.persist(1L, dto, validated);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isInstanceOf(ImageFinalizeRequestedEvent.class);
        ImageFinalizeRequestedEvent event = (ImageFinalizeRequestedEvent) eventCaptor.getValue();
        assertThat(event.pendingKey()).isEqualTo("pending/images/1/a.png");
        assertThat(event.finalKey()).isEqualTo("images/1/a.png");
    }

    @Test
    void pendingImageKey가_없으면_이미지_확정_이벤트를_발행하지_않는다() {
        stubCommonLookups();
        RecordReqDTO.WriteChapterRecord dto =
                new RecordReqDTO.WriteChapterRecord(10L, null, null, null, null, null, Status.DRAFT);
        ValidatedChapterRecord validated = new ValidatedChapterRecord(null, null, "images/1/a.png", null, null);

        chapterRecordWriter.persist(1L, dto, validated);

        verify(applicationEventPublisher, never()).publishEvent(any());
    }

    @Test
    void COMPLETED면_memberStorybook을_완료처리하고_요약_이벤트를_발행한다() {
        stubCommonLookups();
        RecordReqDTO.WriteChapterRecord dto =
                new RecordReqDTO.WriteChapterRecord(10L, Emotion.HAPPY, null, null, null, null, Status.COMPLETED);
        ValidatedChapterRecord validated = new ValidatedChapterRecord(null, null, null, null, null);

        chapterRecordWriter.persist(1L, dto, validated);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isInstanceOf(ChapterCompletedEvent.class);
    }

    @Test
    void 저장이_충돌하면_DUPLICATE_MEMBER_CHAPTER_예외로_변환하고_이벤트는_발행하지_않는다() {
        stubCommonLookups();
        doThrow(new DataIntegrityViolationException("duplicate"))
                .when(memberChapterRepository).save(any());
        RecordReqDTO.WriteChapterRecord dto =
                new RecordReqDTO.WriteChapterRecord(10L, null, null, null, null, null, Status.DRAFT);
        ValidatedChapterRecord validated = new ValidatedChapterRecord(null, null, null, null, null);

        assertThatThrownBy(() -> chapterRecordWriter.persist(1L, dto, validated))
                .isInstanceOf(RecordException.class)
                .satisfies(e -> assertThat(((RecordException) e).getErrorCode()).isEqualTo(RecordErrorCode.DUPLICATE_MEMBER_CHAPTER));

        verify(applicationEventPublisher, never()).publishEvent(any());
    }

    @Test
    void 확정_리스너가_먼저_final키로_바꿔놨으면_다시_읽은_값이_final이라_pending으로_되돌리지_않는다() {
        stubCommonLookups();
        MemberChapter existing = MemberChapter.builder().id(50L).imageKey("images/1/a.png").build();
        given(memberChapterRepository.findById(50L)).willReturn(Optional.of(existing));

        // validate() 시점에 pending 키를 재사용하려 했지만(경합), findById로 다시 읽은
        // memberChapter.imageKey는 이미 확정 리스너가 final로 갱신해둔 상태
        RecordReqDTO.WriteChapterRecord dto =
                new RecordReqDTO.WriteChapterRecord(10L, null, null, null, null, null, Status.DRAFT);
        ValidatedChapterRecord validated =
                new ValidatedChapterRecord(50L, null, "pending/images/1/a.png", null, null);

        chapterRecordWriter.persist(1L, dto, validated);

        assertThat(existing.getImageKey()).isEqualTo("images/1/a.png");
    }

    @Test
    void 새로_업로드해서_S3검증까지_마친_pending키는_기존_final키가_있어도_그대로_반영된다() {
        stubCommonLookups();
        MemberChapter existing = MemberChapter.builder().id(50L).imageKey("images/1/old.png").build();
        given(memberChapterRepository.findById(50L)).willReturn(Optional.of(existing));

        // 기존 imageKey가 이미 final(images/1/old.png)이어도 다른 업로드라 되돌리면 안 됨
        RecordReqDTO.WriteChapterRecord dto =
                new RecordReqDTO.WriteChapterRecord(10L, null, null, null, null, null, Status.DRAFT);
        ValidatedChapterRecord validated = new ValidatedChapterRecord(
                50L, null, "pending/images/1/new.png", "pending/images/1/new.png", "images/1/new.png");

        chapterRecordWriter.persist(1L, dto, validated);

        assertThat(existing.getImageKey()).isEqualTo("pending/images/1/new.png");
    }
}