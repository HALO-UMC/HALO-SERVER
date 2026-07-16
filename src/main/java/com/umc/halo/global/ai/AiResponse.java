package com.umc.halo.global.ai;

import com.umc.halo.global.ai.exception.AiException;
import com.umc.halo.global.ai.exception.code.AiErrorCode;
import lombok.Getter;

import java.util.List;

@Getter
public class AiResponse {

    private List<Candidate> candidates;

    public String getText() {

        if (candidates == null || candidates.isEmpty()) {
            throw new AiException(AiErrorCode.AI_RESPONSE_INVALID);
        }

        Candidate candidate = candidates.get(0);

        if (candidate.getContent() == null
                || candidate.getContent().getParts() == null
                || candidate.getContent().getParts().isEmpty()) {
            throw new AiException(AiErrorCode.AI_RESPONSE_INVALID);
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
