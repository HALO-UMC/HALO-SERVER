package com.umc.halo.domain.content.storybook.dto.response;

import com.umc.halo.domain.content.storybook.enums.BookshelfStatus;
import com.umc.halo.domain.content.storybook.enums.HomeStatus;

import java.util.List;

public class HomeResponse {

    public record GetHome(
            HomeStatus homeStatus,
            String memberName,
            RepresentativeStorybook representativeStorybook,
            int otherInProgressCount,
            List<BookshelfItem> bookshelf,
            List<RecommendedStorybook> recommendedStorybooks
    ) {}

    public record RepresentativeStorybook(
            Long storybookId,
            String title,
            String currentChapterTitle,
            int currentChapterOrder,
            boolean todayAvailable
    ) {}

    public record BookshelfItem(
            Long storybookId,
            String title,
            int themeOrder,
            String spineColor,
            BookshelfStatus status
    ) {}

    public record RecommendedStorybook(
            Long storybookId,
            String title,
            String shortDescription,
            String imageUrl,
            String recommendationReasonText
    ) {}
}