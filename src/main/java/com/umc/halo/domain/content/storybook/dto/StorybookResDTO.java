package com.umc.halo.domain.content.storybook.dto;

import com.umc.halo.domain.content.storybook.enums.BookshelfStatus;
import com.umc.halo.domain.content.storybook.enums.ChapterViewStatus;
import com.umc.halo.domain.content.storybook.enums.HomeStatus;
import com.umc.halo.domain.content.storybook.enums.StorybookStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public class StorybookResDTO {

    @Builder
    public record GetHome(
            HomeStatus homeStatus,
            String memberName,
            RepresentativeStorybook representativeStorybook,
            int otherInProgressCount,
            List<BookshelfItem> bookshelf,
            List<RecommendedStorybook> recommendedStorybooks
    ) {}

    @Builder
    public record RepresentativeStorybook(
            Long storybookId,
            String title,
            String currentChapterTitle,
            int currentChapterOrder,
            boolean todayAvailable
    ) {}

    @Builder
    public record BookshelfItem(
            Long storybookId,
            String title,
            int themeOrder,
            String spineColor,
            BookshelfStatus status
    ) {}

    @Builder
    public record GetStorybookList(
            List<StorybookSummary> storybooks,
            List<SituationalRecommendation> situationalRecommendations
    ) {}

    @Builder
    public record StorybookSummary(
            Long storybookId,
            String title,
            int themeOrder,
            String shortDescription,
            String imageUrl,
            StorybookStatus status,
            Integer lastChapterOrder,
            LocalDate lastCompletedDate
    ) {}

    @Builder
    public record SituationalRecommendation(
            String tag,
            List<SituationalStorybook> storybooks
    ) {}

    @Builder
    public record SituationalStorybook(
            Long storybookId,
            String title,
            String imageUrl,
            String recommendationReasonText
    ) {}

    @Builder
    public record GetStorybookDetail(
            Long storybookId,
            String title,
            String description,
            String imageUrl,
            List<ChapterInfo> chapters
    ) {}

    @Builder
    public record ChapterInfo(
            int chapterOrder,
            String title,
            String imageUrl,
            String shortDescription,
            String description,
            ChapterViewStatus status
    ) {}

    @Builder
    public record StartStorybook(
            Long memberStorybookId,
            Long storybookId,
            StorybookStatus status
    ) {}

    @Builder
    public record GetRecommendedStorybooks(
            List<RecommendedStorybook> storybooks
    ) {}

    @Builder
    public record RecommendedStorybook(
            Long storybookId,
            String title,
            String shortDescription,
            String imageUrl,
            String recommendationReasonText
    ) {}
}
