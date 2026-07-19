package com.umc.halo.domain.calendar.dto;

import lombok.Builder;
import java.util.List;

public class CalendarMonthlyResDTO {

    @Builder
    public record MonthlyInfo(
            Stats stats,
            List<Integer> recordedDays,
            List<CompletedStorybook> completedStorybooks
    ) {}

    @Builder
    public record Stats(
            Integer completedPageCount,
            Integer completedStorybookCount,
            Integer inProgressStorybookCount
    ) {}

    @Builder
    public record CompletedStorybook(
            Long storybookId,
            String spineColor
    ) {}
}