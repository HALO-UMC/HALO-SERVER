package com.umc.halo.global.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiService {

    private final AiClient aiClient;

    public String generateChapterSummary(String chapterTitle, List<QuestionAnswer> questionAnswers) {

        String prompt = PromptFactory.chapterSummary(chapterTitle, questionAnswers);

        return aiClient.generate(prompt);
    }
}
