package com.umc.halo.domain.calendar.service;

import com.umc.halo.domain.calendar.converter.*;
import com.umc.halo.domain.calendar.dto.*;
import com.umc.halo.domain.content.storybook.entity.*;
import com.umc.halo.domain.member.entity.*;
import com.umc.halo.domain.member.exception.*;
import com.umc.halo.domain.member.exception.code.*;
import com.umc.halo.domain.member.repository.*;
import com.umc.halo.domain.record.entity.*;
import com.umc.halo.domain.record.enums.*;
import com.umc.halo.domain.record.repository.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private static final int TOTAL_CHAPTER_COUNT = 10;

    private final MemberRepository memberRepository;
    private final MemberChapterRepository memberChapterRepository;
    private final MemberStorybookRepository memberStorybookRepository;

    //월간
    @Transactional(readOnly = true)
    public CalendarMonthlyResDTO.MonthlyInfo getMonthly(Long memberId, int year, int month) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<MemberChapter> completedChapters = memberChapterRepository
                .findByMemberAndStatusAndCompletedDateBetween(member, Status.COMPLETED, startDate, endDate);

        int completedPageCount = completedChapters.size();


        List<MemberStorybook> memberStorybooks = memberStorybookRepository
                .findAllByMemberWithStorybook(member);

        List<MemberStorybook> completedThisMonth = memberStorybooks.stream()
                .filter(ms -> ms.getLastChapterOrder() == TOTAL_CHAPTER_COUNT)
                .filter(ms -> ms.getLastCompletedDate() != null
                        && !ms.getLastCompletedDate().isBefore(startDate)
                        && !ms.getLastCompletedDate().isAfter(endDate))
                .toList();
        List<CalendarMonthlyResDTO.CompletedStorybook> completedStorybooks = completedThisMonth.stream()
                .map(ms -> CalendarConverter.toCompletedStorybook(ms.getStorybook()))
                .toList();

        int completedStorybookCount = completedStorybooks.size();

        int inProgressStorybookCount = (int) memberStorybooks.stream()
                .filter(ms -> ms.getLastChapterOrder() < TOTAL_CHAPTER_COUNT)
                .filter(ms -> ms.getLastCompletedDate() != null
                        && !ms.getLastCompletedDate().isBefore(startDate)
                        && !ms.getLastCompletedDate().isAfter(endDate))
                .count();


        Set<Integer> completedStorybookDays = completedThisMonth.stream()
                .map(ms -> ms.getLastCompletedDate().getDayOfMonth())
                .collect(Collectors.toSet());

        List<CalendarMonthlyResDTO.RecordedDay> recordedDays = completedChapters.stream()
                .map(mc -> mc.getCompletedDate().getDayOfMonth())
                .distinct()
                .sorted()
                .map(day -> CalendarConverter.toRecordedDay(day, completedStorybookDays.contains(day)))
                .toList();

        CalendarMonthlyResDTO.Stats stats = CalendarConverter.toStats(
                completedPageCount, completedStorybookCount, inProgressStorybookCount);

        return CalendarConverter.toMonthlyInfo(stats, recordedDays, completedStorybooks);
    }

    // 일별
    @Transactional(readOnly = true)
    public CalendarDailyResDTO.DailyInfo getDaily(Long memberId, LocalDate date) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        List<MemberChapter> dayChapters = memberChapterRepository
                .findDailyWithStorybook(member, Status.COMPLETED, date).stream()
                .sorted(Comparator.comparing(MemberChapter::getUpdatedAt).reversed())
                .toList();

        Map<Long, MemberStorybook> progressByStorybookId = memberStorybookRepository
                .findAllByMemberWithStorybook(member).stream()
                .collect(Collectors.toMap(ms -> ms.getStorybook().getId(), Function.identity()));

        Map<Long, List<MemberChapter>> chaptersByStorybook = memberChapterRepository
                .findAllByMemberWithChapter(member).stream()
                .collect(Collectors.groupingBy(mc -> mc.getChapter().getStorybook().getId()));
        List<CalendarDailyResDTO.StorybookInfo> storybooks = new ArrayList<>();
        List<CalendarDailyResDTO.ChapterInfo> chapters = new ArrayList<>();

        for (MemberChapter mc : dayChapters) {
            Storybook storybook = mc.getChapter().getStorybook();
            int chapterOrderOfDay = mc.getChapter().getChapterOrder();

            if (chapterOrderOfDay == TOTAL_CHAPTER_COUNT) {
                storybooks.add(CalendarConverter.toStorybookInfo(storybook));
            } else {
                MemberStorybook ms = progressByStorybookId.get(storybook.getId());
                List<MemberChapter> myChapters = chaptersByStorybook.getOrDefault(storybook.getId(), List.of());
                Integer nextChapterOrder = resolveNextChapterOrder(ms, myChapters, chapterOrderOfDay);
                chapters.add(CalendarConverter.toChapterInfo(storybook, nextChapterOrder));
            }
        }

        return CalendarConverter.toDailyInfo(date.toString(), storybooks, chapters);
    }

    private Integer resolveNextChapterOrder(MemberStorybook ms, List<MemberChapter> myChapters, int fallbackOrder) {
        Integer lastChapterOrder = (ms != null) ? ms.getLastChapterOrder() : fallbackOrder;

        boolean lastCompleted = myChapters.stream()
                .anyMatch(mc -> lastChapterOrder.equals(mc.getChapter().getChapterOrder())
                        && mc.getStatus() == Status.COMPLETED);

        int next = lastCompleted ? lastChapterOrder + 1 : lastChapterOrder;
        return Math.min(next, TOTAL_CHAPTER_COUNT);
    }
}