package com.umc.halo.global.ai.service;

import com.umc.halo.global.ai.AiClient;
import com.umc.halo.global.ai.QuestionAnswer;
import com.umc.halo.global.ai.exception.AiException;
import com.umc.halo.global.ai.exception.code.AiErrorCode;
import com.umc.halo.global.ai.factory.PromptFactory;
import com.umc.halo.global.ai.filter.SensitiveDataFilter;
import com.umc.halo.global.rateLimit.AiRateLimitType;
import com.umc.halo.global.rateLimit.RateLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiService {

    private final AiClient aiClient;
    private final SensitiveDataFilter sensitiveDataFilter;
    private final RateLimitService rateLimitService;

    public String generateChapterSummary(Long memberId, String themeName, String chapterTitle, String pageIntro, List<QuestionAnswer> questionAnswers, String emotionTag) {

        if (!rateLimitService.tryConsume(memberId, AiRateLimitType.CHAPTER_SUMMARY)) {
            throw new AiException(AiErrorCode.AI_RATE_LIMIT_EXCEEDED);
        }

        List<QuestionAnswer> filteredAnswers =
                questionAnswers.stream()
                        .map(q ->
                                new QuestionAnswer(
                                        q.question(),
                                        sensitiveDataFilter.mask(q.answer())
                                )
                        )
                        .toList();

        String prompt = PromptFactory.chapterSummary(themeName, chapterTitle, pageIntro, filteredAnswers, emotionTag);

        return aiClient.generate(prompt);
    }

    public String generateAnniversaryNotificationMessage(Long memberId, String title, String memo) {

        if (!rateLimitService.tryConsume(memberId, AiRateLimitType.ANNIVERSARY_MESSAGE)) {
            throw new AiException(AiErrorCode.AI_RATE_LIMIT_EXCEEDED);
        }

        String filteredTitle = sensitiveDataFilter.mask(title);
        String filteredMemo = sensitiveDataFilter.mask(memo);

        String prompt = PromptFactory.anniversaryNotification(filteredTitle, filteredMemo);

        return aiClient.generate(prompt);
    }
}
