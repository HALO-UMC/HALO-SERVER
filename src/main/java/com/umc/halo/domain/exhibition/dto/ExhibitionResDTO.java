package com.umc.halo.domain.exhibition.dto;

import java.time.LocalDate;
import java.util.List;

public class ExhibitionResDTO {

    public record MainInfo(
            Stats stats,
            Long currentStorybookId,
            List<CompletedStorybook> storybooks,
            List<InProgressStorybook> inProgressStorybooks,
            List<RecommendedStorybook> recommendedStorybooks
    ) {
    }

    public record Stats(
            Integer collectedCharacterCount,
            Integer inProgressStorybookCount
    ) {
    }

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

    public record InProgressStorybook(
            Long storybookId,
            String title,
            Integer nextChapterOrder,
            Boolean todayAvailable
    ) {
    }

    public record RecommendedStorybook(
            Long storybookId,
            String title,
            String shortDescription,
            String imageUrl,
            String recommendationReasonText
    ) {
    }
}