package com.umc.halo.domain.content.storybook.dto.response;

import com.umc.halo.domain.content.storybook.enums.ChapterViewStatus;

import java.util.List;

public class StorybookDetailResponse {

    public record GetStorybookDetail(
            Long storybookId,
            String title,
            String description,
            String imageUrl,
            List<ChapterInfo> chapters
    ) {}

    public record ChapterInfo(
            int chapterOrder,
            String title,
            String imageUrl,
            String shortDescription,
            String description,
            ChapterViewStatus status
    ) {}
}