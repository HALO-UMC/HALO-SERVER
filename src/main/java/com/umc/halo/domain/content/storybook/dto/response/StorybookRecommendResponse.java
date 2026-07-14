package com.umc.halo.domain.content.storybook.dto.response;

import java.util.List;

public class StorybookRecommendResponse {

    public record GetRecommendedStorybooks(
            List<RecommendedStorybook> storybooks
    ) {}

    public record RecommendedStorybook(
            Long storybookId,
            String title,
            String shortDescription,
            String imageUrl,
            String recommendationReasonText
    ) {}
}