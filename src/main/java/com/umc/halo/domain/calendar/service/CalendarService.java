package com.umc.halo.domain.calendar.service;

import com.umc.halo.domain.calendar.converter.CalendarConverter;
import com.umc.halo.domain.calendar.dto.CalendarDailyResDTO;
import com.umc.halo.domain.calendar.dto.CalendarMonthlyResDTO;
import com.umc.halo.domain.content.storybook.entity.Storybook;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.exception.MemberException;
import com.umc.halo.domain.member.exception.code.MemberErrorCode;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.record.entity.MemberChapter;
import com.umc.halo.domain.record.entity.MemberStorybook;
import com.umc.halo.domain.record.enums.Status;
import com.umc.halo.domain.record.repository.MemberChapterRepository;
import com.umc.halo.domain.record.repository.MemberStorybookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
                List<MemberChapter> myChapters = chaptersByStorybook.getOrDefault(storybook.getId(), List.of());
                Integer completedChapterOrder = resolveCompletedChapterOrder(myChapters);
                chapters.add(CalendarConverter.toChapterInfo(storybook, completedChapterOrder));
            }
        }

        return CalendarConverter.toDailyInfo(date.toString(), storybooks, chapters);
    }
    private Integer resolveCompletedChapterOrder(List<MemberChapter> myChapters) {
        return myChapters.stream()
                .filter(mc -> mc.getStatus() == Status.COMPLETED)
                .map(mc -> mc.getStorybookChapter().getChapterOrder())
                .max(Integer::compareTo)
                .orElse(0);
    }

}
