package com.umc.halo.global.ai;

import lombok.Getter;

import java.util.List;

@Getter
public class AiResponse {

    private List<Candidate> candidates;

    public String getText() {

        if (candidates == null || candidates.isEmpty()) {
            throw new IllegalStateException("Gemini 응답이 비어 있습니다.");
        }

        Candidate candidate = candidates.get(0);

        if (candidate.getContent() == null
                || candidate.getContent().getParts() == null
                || candidate.getContent().getParts().isEmpty()) {
            throw new IllegalStateException("Gemini 응답 형식이 올바르지 않습니다.");
        }

        return candidate.getContent()
                .getParts()
                .get(0)
                .getText();
    }


    @Getter
    public static class Candidate {
        private Content content;
    }


    @Getter
    public static class Content {
        private List<Part> parts;
    }


    @Getter
    public static class Part {
        private String text;
    }
}
