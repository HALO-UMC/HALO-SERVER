package com.umc.halo.domain.notification.entity;

import com.umc.halo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "common_anniversary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private Boolean isLunar = false;

    @Column(name = "seven_days_alarm_enabled", nullable = false)
    private Boolean sevenDaysAlarmEnabled = true;

    @Column(name = "day_alarm_enabled", nullable = false)
    private Boolean dayAlarmEnabled = true;
}