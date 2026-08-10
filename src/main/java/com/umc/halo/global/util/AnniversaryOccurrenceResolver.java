package com.umc.halo.global.util;

import com.github.usingsky.calendar.KoreanLunarCalendar;
import com.umc.halo.domain.notification.entity.Anniversary;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

public class AnniversaryOccurrenceResolver {

    private static final Object LUNAR_CALENDAR_LOCK = new Object();

    private AnniversaryOccurrenceResolver() {
    }

    // isLunar 여부에 따라 양력/음력 분기해서 다음 발생일 계산
    public static LocalDate resolveNextOccurrence(Anniversary anniversary, LocalDate today) {
        if (Boolean.TRUE.equals(anniversary.getIsLunar())) {
            return resolveNextOccurrenceLunar(anniversary, today);
        }
        return anniversary.resolveNextOccurrence(today);
    }

    private static LocalDate resolveNextOccurrenceLunar(Anniversary anniversary, LocalDate today) {
        int lunarMonth = anniversary.getAnniversaryDate().getMonthValue();
        int lunarDay = anniversary.getAnniversaryDate().getDayOfMonth();

        if (!Boolean.TRUE.equals(anniversary.getIsRepeated())) {
            LocalDate solarDate = convertLunarToSolar(anniversary.getAnniversaryDate().getYear(), lunarMonth, lunarDay);
            return (solarDate != null && !solarDate.isBefore(today)) ? solarDate : null;
        }

        return Stream.of(today.getYear() - 1, today.getYear(), today.getYear() + 1)
                .map(year -> convertLunarToSolar(year, lunarMonth, lunarDay))
                .filter(Objects::nonNull)
                .filter(date -> !date.isBefore(today))
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    // 음력 날짜(윤달 아님)를 해당 연도의 양력 날짜로 변환. 지원 범위를 벗어나거나 변환 실패 시 null 반환
    public static LocalDate convertLunarToSolar(int lunarYear, int lunarMonth, int lunarDay) {
        synchronized (LUNAR_CALENDAR_LOCK) {
            KoreanLunarCalendar calendar = KoreanLunarCalendar.getInstance();
            boolean success = calendar.setLunarDate(lunarYear, lunarMonth, lunarDay, false);
            if (!success) {
                return null;
            }
            return LocalDate.of(calendar.getSolarYear(), calendar.getSolarMonth(), calendar.getSolarDay());
        }
    }
}