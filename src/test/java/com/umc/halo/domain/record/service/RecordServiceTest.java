package com.umc.halo.domain.record.service;

import com.umc.halo.domain.content.chapter.entity.Chapter;
import com.umc.halo.domain.content.chapter.entity.SceneCard;
import com.umc.halo.domain.content.chapter.repository.ChapterQuestionRepository;
import com.umc.halo.domain.content.chapter.repository.ChapterRepository;
import com.umc.halo.domain.content.chapter.repository.SceneCardRepository;
import com.umc.halo.domain.content.chapter.service.ChapterService;
import com.umc.halo.domain.content.storybook.entity.Storybook;
import com.umc.halo.domain.image.service.ImageService;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.record.dto.RecordReqDTO;
import com.umc.halo.domain.record.dto.RecordResDTO;
import com.umc.halo.domain.record.entity.MemberStorybook;
import com.umc.halo.domain.record.enums.CoverType;
import com.umc.halo.domain.record.enums.Status;
import com.umc.halo.domain.record.exception.RecordException;
import com.umc.halo.domain.record.exception.code.RecordErrorCode;
import com.umc.halo.domain.record.repository.MemberChapterAnswerRepository;
import com.umc.halo.domain.record.repository.MemberChapterRepository;
import com.umc.halo.domain.record.repository.MemberStorybookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * validate()는 @Transactional 없이 실행됨
 * 검증 실패 시 실제 DB 쓰기(ChapterRecordWriter.persist)와 S3 확정 경로로 넘어가지 않는지
 * 검증을 통과한 값이 ChapterRecordWriter로 전달되는지를 검증
 */
@ExtendWith(MockitoExtension.class)
class RecordServiceTest {

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
    private ChapterService chapterService;
    @Mock
    private ImageService imageService;
    @Mock
    private ChapterRecordWriter chapterRecordWriter;

    @InjectMocks
    private RecordService recordService;

    private final Member member = Member.builder().id(1L).build();
    private final Storybook storybook = Storybook.builder().id(100L).build();
    private final Chapter chapter = Chapter.builder().id(10L).storybook(storybook).chapterOrder(1).build();

    @Test
    void 오늘_이미_완료했으면_검증에서_바로_예외를_던지고_실제_쓰기는_시도하지_않는다() {
        MemberStorybook memberStorybook = MemberStorybook.builder()
                .id(1000L)
                .lastCompletedDate(LocalDate.now())
                .build();

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(chapterRepository.findById(10L)).willReturn(Optional.of(chapter));
        given(memberStorybookRepository.findByStorybookAndMember(storybook, member))
                .willReturn(Optional.of(memberStorybook));

        RecordReqDTO.WriteChapterRecord dto =
                new RecordReqDTO.WriteChapterRecord(10L, null, null, null, null, null, Status.DRAFT);

        assertThatThrownBy(() -> recordService.writeChapterRecord(1L, dto))
                .isInstanceOf(RecordException.class)
                .satisfies(e -> assertThat(((RecordException) e).getErrorCode()).isEqualTo(RecordErrorCode.ALREADY_COMPLETED_TODAY));

        verify(chapterRecordWriter, never()).persist(any(), any(), any());
        verify(imageService, never()).finalizeImage(any(), any());
    }

    @Test
    void 정상_흐름이면_검증된_값을_그대로_ChapterRecordWriter로_전달한다() {
        MemberStorybook memberStorybook = MemberStorybook.builder().id(1000L).build();
        SceneCard sceneCard = SceneCard.builder().id(5L).chapter(chapter).build();

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(chapterRepository.findById(10L)).willReturn(Optional.of(chapter));
        given(memberStorybookRepository.findByStorybookAndMember(storybook, member))
                .willReturn(Optional.of(memberStorybook));
        given(sceneCardRepository.findById(5L)).willReturn(Optional.of(sceneCard));

        RecordReqDTO.WriteChapterRecord dto =
                new RecordReqDTO.WriteChapterRecord(10L, null, CoverType.SCENE_CARD, null, 5L, null, Status.DRAFT);

        RecordResDTO.WriteChapterRecord expected = new RecordResDTO.WriteChapterRecord(99L, false);
        given(chapterRecordWriter.persist(eq(1L), eq(dto), any())).willReturn(expected);

        RecordResDTO.WriteChapterRecord result = recordService.writeChapterRecord(1L, dto);

        assertThat(result).isEqualTo(expected);

        ArgumentCaptor<ValidatedChapterRecord> captor = ArgumentCaptor.forClass(ValidatedChapterRecord.class);
        verify(chapterRecordWriter).persist(eq(1L), eq(dto), captor.capture());
        assertThat(captor.getValue().sceneCardId()).isEqualTo(5L);
        assertThat(captor.getValue().memberChapterId()).isNull();
        assertThat(captor.getValue().imageKey()).isNull();
        assertThat(captor.getValue().pendingImageKey()).isNull();
    }

    @Test
    void 새_pending_이미지면_finalizeImage_결과가_검증결과에_pendingKey로_담겨_전달된다() {
        MemberStorybook memberStorybook = MemberStorybook.builder().id(1000L).build();

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(chapterRepository.findById(10L)).willReturn(Optional.of(chapter));
        given(memberStorybookRepository.findByStorybookAndMember(storybook, member))
                .willReturn(Optional.of(memberStorybook));
        given(imageService.finalizeImage(1L, "pending/images/1/a.png"))
                .willReturn(new ImageService.FinalizedImage("images/1/a.png", "pending/images/1/a.png"));

        RecordReqDTO.WriteChapterRecord dto = new RecordReqDTO.WriteChapterRecord(
                10L, null, CoverType.IMAGE, "pending/images/1/a.png", null, null, Status.DRAFT);

        given(chapterRecordWriter.persist(eq(1L), eq(dto), any()))
                .willReturn(new RecordResDTO.WriteChapterRecord(1L, false));

        recordService.writeChapterRecord(1L, dto);

        ArgumentCaptor<ValidatedChapterRecord> captor = ArgumentCaptor.forClass(ValidatedChapterRecord.class);
        verify(chapterRecordWriter).persist(eq(1L), eq(dto), captor.capture());
        assertThat(captor.getValue().pendingImageKey()).isEqualTo("pending/images/1/a.png");
        assertThat(captor.getValue().finalImageKey()).isEqualTo("images/1/a.png");

        // 검증 단계에서는 실재 여부 확인(HEAD)만 하고, 실제 copy/delete(mutation)는 절대 호출하지 않는다
        verify(imageService, never()).copyToFinal(any(), any());
        verify(imageService, never()).deleteObject(any());
    }
}