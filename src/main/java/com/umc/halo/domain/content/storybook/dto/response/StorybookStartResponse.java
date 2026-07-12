package com.umc.halo.domain.content.storybook.dto.response;

public class StorybookStartResponse {

    public record StartStorybook(
            Long memberStorybookId,
            Long storybookId,
            int chapterOrder
    ) {}
}