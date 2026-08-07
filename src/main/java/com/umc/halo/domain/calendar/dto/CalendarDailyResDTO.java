package com.umc.halo.domain.calendar.dto;

import lombok.Builder;
import java.util.List;

public class CalendarDailyResDTO {

    @Builder
    public record DailyInfo(
            String date,
            List<StorybookInfo> storybooks,
            List<ChapterInfo> chapters
    ) {}

    @Builder
    public record StorybookInfo(
            Long storybookId,
            String title,
            String storybookImageUrl
    ) {}

    @Builder
    public record ChapterInfo(
            Long memberChapterId,
            Long storybookId,
            String title,
            Integer completedChapterOrder
    ) {}
}