package com.umc.halo.domain.notification.entity;

import com.umc.halo.domain.member.entity.*;
import com.umc.halo.global.entity.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.*;

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

    @Column(length = 255)
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
}
