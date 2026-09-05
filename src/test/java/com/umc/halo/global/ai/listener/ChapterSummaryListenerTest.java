package com.umc.halo.global.ai.listener;

import com.umc.halo.domain.record.entity.MemberChapter;
import com.umc.halo.domain.record.repository.MemberChapterAnswerRepository;
import com.umc.halo.domain.record.repository.MemberChapterRepository;
import com.umc.halo.global.ai.event.ChapterCompletedEvent;
import com.umc.halo.global.ai.exception.AiException;
import com.umc.halo.global.ai.exception.code.AiErrorCode;
import com.umc.halo.global.ai.service.AiService;
import com.umc.halo.global.enums.Emotion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * generateSummary는 @Transactional 없이 실행됨
 * AI 호출 결과가 detached 상태의 memberChapter에 정확히 반영되고 save()로 명시 저장되는지
 * AI 호출 실패 시 예외를 삼키고 저장을 건너뛰는지를 검증
 */
@ExtendWith(MockitoExtension.class)
class ChapterSummaryListenerTest {

    @Mock
    private MemberChapterRepository memberChapterRepository;
    @Mock
    private MemberChapterAnswerRepository memberChapterAnswerRepository;
    @Mock
    private AiService aiService;

    @InjectMocks
    private ChapterSummaryListener chapterSummaryListener;

    private final ChapterCompletedEvent event =
            new ChapterCompletedEvent(1L, 1L, "storybook", "chapter", "desc", Emotion.HAPPY.getDescription());

    @Test
    void AI_요약_생성에_성공하면_memberChapter에_반영하고_명시적으로_저장한다() {
        MemberChapter memberChapter = MemberChapter.builder().id(1L).build();
        given(memberChapterRepository.findById(1L)).willReturn(Optional.of(memberChapter));
        given(memberChapterAnswerRepository.findAllByMemberChapterOrderByQuestionOrder(memberChapter))
                .willReturn(List.of());
        given(aiService.generateChapterSummary(any(), any(), any(), any(), any(), any()))
                .willReturn("생성된 요약");

        chapterSummaryListener.generateSummary(event);

        assertThat(memberChapter.getSummary()).isEqualTo("생성된 요약");
        verify(memberChapterRepository).save(memberChapter);
    }

    @Test
    void AI_호출이_실패하면_예외를_삼키고_저장하지_않는다() {
        MemberChapter memberChapter = MemberChapter.builder().id(1L).build();
        given(memberChapterRepository.findById(1L)).willReturn(Optional.of(memberChapter));
        given(memberChapterAnswerRepository.findAllByMemberChapterOrderByQuestionOrder(memberChapter))
                .willReturn(List.of());
        given(aiService.generateChapterSummary(any(), any(), any(), any(), any(), any()))
                .willThrow(new AiException(AiErrorCode.AI_GENERATE_FAILED));

        assertThatCode(() -> chapterSummaryListener.generateSummary(event))
                .doesNotThrowAnyException();

        verify(memberChapterRepository, never()).save(any());
    }
}