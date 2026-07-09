package com.umc.halo.domain.notification.entity;

import com.umc.halo.global.entity.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "common_anniversary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonAnniversary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "common_anniversary_id")
    private Long id;

    @Column(length = 20, nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer month;

    @Column(nullable = false)
    private Integer day;

    @Column(name = "is_lunar", nullable = false)
    @Builder.Default
    private Boolean isLunar = false;

    @Column(name = "seven_days_alarm_enabled", nullable = false)
    @Builder.Default
    private Boolean sevenDaysAlarmEnabled = true;

    @Column(name = "day_alarm_enabled", nullable = false)
    @Builder.Default
    private Boolean dayAlarmEnabled = true;
}