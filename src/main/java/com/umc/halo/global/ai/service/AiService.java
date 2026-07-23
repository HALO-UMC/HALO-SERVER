package com.umc.halo.global.ai.service;

import com.umc.halo.global.ai.AiClient;
import com.umc.halo.global.ai.QuestionAnswer;
import com.umc.halo.global.ai.factory.PromptFactory;
import com.umc.halo.global.ai.filter.SensitiveDataFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiService {

    private final AiClient aiClient;
    private final SensitiveDataFilter sensitiveDataFilter;

    public String generateChapterSummary(String themeName, String chapterTitle, String pageIntro, List<QuestionAnswer> questionAnswers, String emotionTag) {

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

    public String generateAnniversaryNotificationMessage(String title, String memo) {

        String filteredMemo = sensitiveDataFilter.mask(memo);

        String prompt = PromptFactory.anniversaryNotification(title, filteredMemo);

        return aiClient.generate(prompt);
    }
}
