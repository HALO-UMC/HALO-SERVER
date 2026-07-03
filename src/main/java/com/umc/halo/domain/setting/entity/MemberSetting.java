package com.umc.halo.domain.setting.entity;

import com.umc.halo.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "member_setting")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_setting_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bgm_id")
    private Bgm bgm;

    @Column(name = "bgm_enabled", nullable = false)
    private Boolean bgmEnabled = true;

    @Column(name = "bgm_volume", nullable = false)
    private Integer bgmVolume = 100;

    @Column(name = "regular_notification_enabled", nullable = false)
    private Boolean regularNotificationEnabled = true;

    @Column(name = "regular_notification_time")
    private LocalTime regularNotificationTime;

    @Column(name = "today_chapter_notification_enabled", nullable = false)
    private Boolean todayChapterNotificationEnabled = true;

    @Column(name = "retention_notification_enabled", nullable = false)
    private Boolean retentionNotificationEnabled = true;

    @Column(name = "anniversary_notification_enabled", nullable = false)
    private Boolean anniversaryNotificationEnabled = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}