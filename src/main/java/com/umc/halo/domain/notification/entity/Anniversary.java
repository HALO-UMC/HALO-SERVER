package com.umc.halo.domain.notification.entity;

import com.umc.halo.domain.member.entity.*;
import com.umc.halo.global.entity.*;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.*;

@Slf4j
@Entity
@Table(name = "anniversary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Anniversary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "anniversary_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 20, nullable = false)
    private String title;

    @Column(name = "anniversary_date", nullable = false)
    private LocalDate anniversaryDate;

    @Column(name = "is_repeated", nullable = false)
    @Builder.Default
    private Boolean isRepeated = true;

    @Column(name = "is_lunar", nullable = false)
    @Builder.Default
    private Boolean isLunar = false;

    @Column(name = "seven_days_alarm_enabled", nullable = false)
    @Builder.Default
    private Boolean sevenDaysAlarmEnabled = true;

    @Column(name = "day_alarm_enabled", nullable = false)
    @Builder.Default
    private Boolean dayAlarmEnabled = true;

    @Column(length = 50)
    private String memo;

    public void update(String title, LocalDate anniversaryDate, Boolean isLunar, Boolean isRepeated,
                       Boolean sevenDaysAlarmEnabled, Boolean dayAlarmEnabled, String memo) {
        this.title = title;
        this.anniversaryDate = anniversaryDate;
        this.isLunar = isLunar;
        this.isRepeated = isRepeated;
        this.sevenDaysAlarmEnabled = sevenDaysAlarmEnabled;
        this.dayAlarmEnabled = dayAlarmEnabled;
        this.memo = memo;
    }

    // 반복 기념일의 다음(오늘 이후) 발생일 계산. 윤년(2/29) 기념일은 평년엔 2/28로 대체
    public LocalDate resolveNextOccurrence(LocalDate today) {

        if (!Boolean.TRUE.equals(isRepeated)) {
            return anniversaryDate.isBefore(today) ? null : anniversaryDate;
        }

        LocalDate thisYear;

        try {
            thisYear = anniversaryDate.withYear(today.getYear());
        } catch (DateTimeException e) {
            log.warn("윤년 기념일 올해 날짜 계산 실패, 2/28로 대체. anniversaryId={}, year={}", id, today.getYear());
            thisYear = LocalDate.of(today.getYear(), 2, 28);
        }

        if (!thisYear.isBefore(today)) {
            return thisYear;
        }

        try {
            return anniversaryDate.withYear(today.getYear() + 1);
        } catch (DateTimeException e) {
            log.warn("윤년 기념일 다음 해 날짜 계산 실패, 2/28로 대체. anniversaryId={}, year={}", id, today.getYear() + 1);
            return LocalDate.of(today.getYear() + 1, 2, 28);
        }
    }
}
