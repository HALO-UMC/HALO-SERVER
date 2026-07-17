package com.umc.halo.global.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiService {

    private final AiClient aiClient;

    public String generateChapterSummary(String themeName, String chapterTitle, String pageIntro, List<QuestionAnswer> questionAnswers, String emotionTag) {

        String prompt = PromptFactory.chapterSummary(themeName, chapterTitle, pageIntro, questionAnswers, emotionTag);

        return aiClient.generate(prompt);
    }
}
