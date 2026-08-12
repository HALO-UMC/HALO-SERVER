package com.umc.halo.domain.exhibition.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public class ExhibitionResDTO {

    @Builder
    public record MainInfo(
            Stats stats,
            Long currentStorybookId,
            List<CompletedStorybook> storybooks,
            List<InProgressStorybook> inProgressStorybooks,
            List<RecommendedStorybook> recommendedStorybooks
    ) {
    }

    @Builder
    public record Stats(
            Integer collectedCharacterCount,
            Integer inProgressStorybookCount
    ) {
    }

    @Builder
    public record CompletedStorybook(
            Long storybookId,
            String title,
            String summary,
            LocalDate completedDate,
            Long characterId,
            String characterName,
            String characterImageUrl
    ) {
    }

    @Builder
    public record InProgressStorybook(
            Long storybookId,
            String title,
            Integer nextChapterOrder,
            Boolean todayAvailable
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