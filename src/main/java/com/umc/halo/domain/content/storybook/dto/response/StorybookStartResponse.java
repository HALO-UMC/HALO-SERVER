package com.umc.halo.domain.content.storybook.dto.response;

import com.umc.halo.domain.content.storybook.enums.StorybookStatus;

public class StorybookStartResponse {

    public record StartStorybook(
            Long memberStorybookId,
            Long storybookId,
            StorybookStatus status
    ) {}
}