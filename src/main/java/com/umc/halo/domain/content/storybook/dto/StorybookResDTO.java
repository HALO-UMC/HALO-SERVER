package com.umc.halo.domain.content.storybook.dto;

import com.umc.halo.domain.content.storybook.enums.*;
import lombok.*;

import java.time.*;
import java.util.*;

public class StorybookResDTO {

    private StorybookResDTO() {
        throw new IllegalStateException("유틸리티 클래스는 인스턴스화할 수 없습니다.");
    }

    @Builder
    public record GetHome(
            HomeStatus homeStatus,
            String memberName,
            List<BookshelfItem> bookshelf,
            List<InProgressStorybook> inProgressStorybooks,
            List<RecommendedStorybook> recommendedStorybooks
    ) {
    }

    @Builder
    public record BookshelfItem(
            Long storybookId,
            String title,
            String shortDescription,
            String imageUrl,
            Integer currentChapterOrder,
            boolean todayAvailable,
            String recommendationReasonText
    ) {
    }

    @Builder
    public record InProgressStorybook(
            Long storybookId,
            String title,
            String imageUrl,
            Integer currentChapterOrder,
            int totalChapterCount,
            boolean todayAvailable
    ) {
    }

    @Builder
    public record GetStorybookList(
            List<StorybookSummary> storybooks,
            List<SituationalRecommendation> situationalRecommendations
    ) {
    }

    @Builder
    public record StorybookSummary(
            Long storybookId,
            String title,
            String shortDescription,
            String imageUrl,
            StorybookStatus status,
            Integer lastChapterOrder,
            LocalDate startedDate,
            LocalDate lastCompletedDate
    ) {
    }

    @Builder
    public record SituationalRecommendation(
            String tag,
            List<SituationalStorybook> storybooks
    ) {
    }

    @Builder
    public record SituationalStorybook(
            Long storybookId,
            String title,
            String imageUrl,
            String recommendationReasonText
    ) {
    }

    @Builder
    public record GetStorybookDetail(
            Long storybookId,
            String title,
            String description,
            String imageUrl,
            int completedChapterCount,
            int progressPercentage,
            List<ChapterInfo> chapters
    ) {
    }

    @Builder
    public record ChapterInfo(
            int chapterOrder,
            Long memberChapterId,
            String title,
            String shortImageUrl,
            String shortDescription,
            String description,
            ChapterViewStatus status
    ) {
    }

    @Builder
    public record StartStorybook(
            Long memberStorybookId,
            Long storybookId,
            StorybookStatus status
    ) {
    }

    @Builder
    public record GetRecommendedStorybooks(
            List<RecommendedStorybook> storybooks
    ) {
    }

    @Builder
    public record RecommendedStorybook(
            Long storybookId,
            String title,
            String shortDescription,
            String imageUrl,
            String recommendationReasonText
    ) {
    }
}