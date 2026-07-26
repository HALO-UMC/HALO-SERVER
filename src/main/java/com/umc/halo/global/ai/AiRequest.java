package com.umc.halo.global.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AiRequest {

    private List<Content> contents;

    public static AiRequest from(String prompt) {

        return new AiRequest(
                List.of(new Content(List.of(new Part(prompt))))
        );
    }

    @Getter
    @AllArgsConstructor
    static class Content {
        private List<Part> parts;
    }


    @Getter
    @AllArgsConstructor
    static class Part {
        private String text;
    }
}
