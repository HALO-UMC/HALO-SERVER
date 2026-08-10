package com.umc.halo.domain.record.service;

import com.umc.halo.domain.content.chapter.entity.Chapter;
import com.umc.halo.domain.content.chapter.service.ChapterService;
import com.umc.halo.domain.content.chapter.repository.ChapterRepository;
import com.umc.halo.domain.content.storybook.entity.Storybook;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.record.entity.MemberStorybook;
import com.umc.halo.domain.record.exception.RecordException;
import com.umc.halo.domain.record.exception.code.RecordErrorCode;
import com.umc.halo.domain.record.repository.MemberChapterRepository;
import com.umc.halo.domain.record.repository.MemberStorybookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * RecordService.validate()가 트랜잭션 없이 도는 동안 Chapter.getStorybook() 같은 LAZY 연관에 접근하도록
 * 필요한 조회, 검증이 이 안에서 끝내는지 검증
 */
@ExtendWith(MockitoExtension.class)
class RecordValidationReaderTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ChapterRepository chapterRepository;
    @Mock
    private MemberChapterRepository memberChapterRepository;
    @Mock
    private MemberStorybookRepository memberStorybookRepository;
    @Mock
    private ChapterService chapterService;

    @InjectMocks
    private RecordValidationReader reader;

    private final Member member = Member.builder().id(1L).build();
    private final Storybook storybook = Storybook.builder().id(100L).build();
    private final Chapter chapter = Chapter.builder().id(10L).storybook(storybook).chapterOrder(3).build();

    @Test
    void 오늘_이미_완료했으면_예외를_던진다() {
        MemberStorybook memberStorybook = MemberStorybook.builder()
                .id(1000L)
                .lastCompletedDate(LocalDate.now())
                .build();

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(chapterRepository.findById(10L)).willReturn(Optional.of(chapter));
        given(memberStorybookRepository.findByStorybookAndMember(storybook, member))
                .willReturn(Optional.of(memberStorybook));

        assertThatThrownBy(() -> reader.load(1L, 10L))
                .isInstanceOf(RecordException.class)
                .satisfies(e -> assertThat(((RecordException) e).getErrorCode()).isEqualTo(RecordErrorCode.ALREADY_COMPLETED_TODAY));
    }

    @Test
    void 정상이면_chapterService_검증을_거쳐_컨텍스트를_반환한다() {
        MemberStorybook memberStorybook = MemberStorybook.builder().id(1000L).build();

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(chapterRepository.findById(10L)).willReturn(Optional.of(chapter));
        given(memberStorybookRepository.findByStorybookAndMember(storybook, member))
                .willReturn(Optional.of(memberStorybook));
        given(memberChapterRepository.findByMemberAndChapter(member, chapter)).willReturn(null);

        RecordValidationReader.LoadedRecordContext context = reader.load(1L, 10L);

        assertThat(context.member()).isEqualTo(member);
        assertThat(context.chapter()).isEqualTo(chapter);
        assertThat(context.memberChapter()).isNull();
        verify(chapterService).validateChapterStatus(member, storybook, 3, null, memberStorybook);
    }
}