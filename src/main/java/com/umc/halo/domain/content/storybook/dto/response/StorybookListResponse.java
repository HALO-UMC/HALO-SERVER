package com.umc.halo.domain.content.storybook.dto.response;

import com.umc.halo.domain.content.storybook.enums.StorybookStatus;

import java.time.LocalDate;
import java.util.List;

public class StorybookListResponse {

    public record GetStorybookList(
            List<StorybookSummary> storybooks,
            List<SituationalRecommendation> situationalRecommendations
    ) {}

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

    public record SituationalRecommendation(
            String tag,
            List<RecommendedStorybook> storybooks
    ) {}

    public record RecommendedStorybook(
            Long storybookId,
            String title,
            String imageUrl,
            String recommendationReasonText
    ) {}
}