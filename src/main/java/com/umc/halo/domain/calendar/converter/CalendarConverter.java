package com.umc.halo.domain.calendar.converter;

import com.umc.halo.domain.calendar.dto.CalendarDailyResDTO;
import com.umc.halo.domain.calendar.dto.CalendarMonthlyResDTO;
import com.umc.halo.domain.content.storybook.entity.Storybook;

import java.util.List;

public class CalendarConverter {

    // 월별
    public static CalendarMonthlyResDTO.Stats toStats(
            int completedPageCount, int completedStorybookCount, int inProgressStorybookCount) {
        return CalendarMonthlyResDTO.Stats.builder()
                .completedPageCount(completedPageCount)
                .completedStorybookCount(completedStorybookCount)
                .inProgressStorybookCount(inProgressStorybookCount)
                .build();
    }
    public static CalendarMonthlyResDTO.RecordedDay toRecordedDay(int day, boolean hasCompletedStorybook) {
        return CalendarMonthlyResDTO.RecordedDay.builder()
                .day(day)
                .hasCompletedStorybook(hasCompletedStorybook)
                .build();
    }

    public static CalendarMonthlyResDTO.CompletedStorybook toCompletedStorybook(Storybook storybook) {
        return CalendarMonthlyResDTO.CompletedStorybook.builder()
                .storybookId(storybook.getId())
                .build();
    }

    public static CalendarMonthlyResDTO.MonthlyInfo toMonthlyInfo(
            CalendarMonthlyResDTO.Stats stats,
            List<CalendarMonthlyResDTO.RecordedDay> recordedDays,
            List<CalendarMonthlyResDTO.CompletedStorybook> completedStorybooks) {
        return CalendarMonthlyResDTO.MonthlyInfo.builder()
                .stats(stats)
                .recordedDays(recordedDays)
                .completedStorybooks(completedStorybooks)
                .build();
    }

    // 일별
    public static CalendarDailyResDTO.StorybookInfo toStorybookInfo(Storybook storybook) {
        return CalendarDailyResDTO.StorybookInfo.builder()
                .storybookId(storybook.getId())
                .title(storybook.getTitle())
                .storybookImageUrl(storybook.getImageUrl())
                .build();
    }

    public static CalendarDailyResDTO.ChapterInfo toChapterInfo(Storybook storybook, Integer nextChapterOrder) {
        return CalendarDailyResDTO.ChapterInfo.builder()
                .storybookId(storybook.getId())
                .title(storybook.getTitle())
                .nextChapterOrder(nextChapterOrder)
                .build();
    }

    public static CalendarDailyResDTO.DailyInfo toDailyInfo(
            String date,
            List<CalendarDailyResDTO.StorybookInfo> storybooks,
            List<CalendarDailyResDTO.ChapterInfo> chapters) {
        return CalendarDailyResDTO.DailyInfo.builder()
                .date(date)
                .storybooks(storybooks)
                .chapters(chapters)
                .build();
    }
}