package com.umc.halo.global.ai.service;

import com.umc.halo.global.ai.AiClient;
import com.umc.halo.global.ai.QuestionAnswer;
import com.umc.halo.global.ai.exception.AiException;
import com.umc.halo.global.ai.exception.code.AiErrorCode;
import com.umc.halo.global.ai.filter.SensitiveDataFilter;
import com.umc.halo.global.rateLimit.AiRateLimitType;
import com.umc.halo.global.rateLimit.RateLimitService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * AiService가 (1) 호출 전 RateLimitService로 한도를 확인하고,
 * (2) 답변/제목/메모를 SensitiveDataFilter로 마스킹한 뒤 프롬프트를 만들어 AiClient에 넘기는지 검증.
 */
@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    @Mock
    private AiClient aiClient;
    @Mock
    private SensitiveDataFilter sensitiveDataFilter;
    @Mock
    private RateLimitService rateLimitService;

    @InjectMocks
    private AiService aiService;

    // ===== generateChapterSummary =====

    @Test
    void generateChapterSummary는_호출_한도를_초과하면_예외를_던지고_AiClient를_호출하지_않는다() {
        given(rateLimitService.tryConsume(1L, AiRateLimitType.CHAPTER_SUMMARY)).willReturn(false);

        assertThatThrownBy(() -> aiService.generateChapterSummary(
                1L, "테마", "챕터", "도입문", List.of(new QuestionAnswer("질문", "답변")), "즐거웠어요"))
                .isInstanceOf(AiException.class)
                .satisfies(e -> assertThat(((AiException) e).getErrorCode()).isEqualTo(AiErrorCode.AI_RATE_LIMIT_EXCEEDED));

        verifyNoInteractions(aiClient);
        verifyNoInteractions(sensitiveDataFilter);
    }

    @Test
    void generateChapterSummary는_한도_내이면_답변을_마스킹한_뒤_프롬프트를_생성해_AiClient에_전달한다() {
        given(rateLimitService.tryConsume(1L, AiRateLimitType.CHAPTER_SUMMARY)).willReturn(true);
        given(sensitiveDataFilter.mask("제 번호는 010-1234-5678이에요")).willReturn("제 번호는 [전화번호]이에요");
        given(aiClient.generate(anyString())).willReturn("생성된 요약");

        String result = aiService.generateChapterSummary(
                1L, "테마", "챕터", "도입문",
                List.of(new QuestionAnswer("오늘 있었던 일은?", "제 번호는 010-1234-5678이에요")),
                "즐거웠어요");

        assertThat(result).isEqualTo("생성된 요약");

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(aiClient).generate(promptCaptor.capture());
        String prompt = promptCaptor.getValue();

        // 마스킹된 텍스트는 프롬프트에 들어가고, 원본 전화번호는 절대 들어가면 안 됨
        assertThat(prompt).contains("[전화번호]");
        assertThat(prompt).doesNotContain("010-1234-5678");
    }

    // ===== generateAnniversaryNotificationMessage =====

    @Test
    void generateAnniversaryNotificationMessage는_호출_한도를_초과하면_예외를_던지고_AiClient를_호출하지_않는다() {
        given(rateLimitService.tryConsume(1L, AiRateLimitType.ANNIVERSARY_MESSAGE)).willReturn(false);

        assertThatThrownBy(() -> aiService.generateAnniversaryNotificationMessage(1L, "생일", "메모"))
                .isInstanceOf(AiException.class)
                .satisfies(e -> assertThat(((AiException) e).getErrorCode()).isEqualTo(AiErrorCode.AI_RATE_LIMIT_EXCEEDED));

        verifyNoInteractions(aiClient);
        verifyNoInteractions(sensitiveDataFilter);
    }

    @Test
    void generateAnniversaryNotificationMessage는_한도_내이면_제목과_메모를_마스킹한_뒤_프롬프트를_생성해_AiClient에_전달한다() {
        given(rateLimitService.tryConsume(1L, AiRateLimitType.ANNIVERSARY_MESSAGE)).willReturn(true);
        given(sensitiveDataFilter.mask("생일 (test@example.com)")).willReturn("생일 ([이메일])");
        given(sensitiveDataFilter.mask("연락주세요 test@example.com")).willReturn("연락주세요 [이메일]");
        given(aiClient.generate(anyString())).willReturn("생성된 알림 문구");

        String result = aiService.generateAnniversaryNotificationMessage(
                1L, "생일 (test@example.com)", "연락주세요 test@example.com");

        assertThat(result).isEqualTo("생성된 알림 문구");

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(aiClient).generate(promptCaptor.capture());
        String prompt = promptCaptor.getValue();

        assertThat(prompt).contains("[이메일]");
        assertThat(prompt).doesNotContain("test@example.com");
    }
}