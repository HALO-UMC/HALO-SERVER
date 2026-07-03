package com.umc.halo.domain.notification.entity;

import com.umc.halo.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "anniversary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Anniversary {

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Target target;

    @Column(name = "is_repeated", nullable = false)
    private Boolean isRepeated = true;

    @Column(name = "seven_days_alarm_enabled", nullable = false)
    private Boolean sevenDaysAlarmEnabled = true;

    @Column(name = "day_alarm_enabled", nullable = false)
    private Boolean dayAlarmEnabled = true;

    @Column(length = 255)
    private String memo;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Target { MOTHER, FATHER, FAMILY }
}