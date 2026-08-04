package com.umc.halo.domain.notification.entity;

import com.umc.halo.domain.member.entity.*;
import com.umc.halo.domain.notification.enums.*;
import com.umc.halo.global.entity.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.*;

@Entity
@Table(
        name = "notification",
        uniqueConstraints = @UniqueConstraint(columnNames = {"anniversary_id", "notification_type", "scheduled_at"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anniversary_id")
    private Anniversary anniversary;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 255, nullable = false)
    private String message;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.SCHEDULED;

    @Column(name = "setting_enabled", nullable = false)
    private Boolean settingEnabled;

    @Column(name = "anniversary_enabled", nullable = false)
    private Boolean anniversaryEnabled;

    public void updateContent(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public void reserve(LocalDateTime scheduledAt) {
        if (this.status == NotificationStatus.SENT) {
            return;
        }
        this.scheduledAt = scheduledAt;
        this.status = NotificationStatus.SCHEDULED;
    }

    public void updateScheduledAt(LocalDateTime scheduledAt) {
        if (this.status == NotificationStatus.SENT) {
            return;
        }
        this.scheduledAt = scheduledAt;
    }

    public void updateSettingEnabled(boolean enabled) {
        this.settingEnabled = enabled;
    }

    public void updateAnniversaryEnabled(boolean enabled) {
        this.anniversaryEnabled = enabled;
    }

    public void expire() {
        if (this.status != NotificationStatus.SENT) {
            this.status = NotificationStatus.EXPIRED;
        }
    }

    public void send() {
        this.status = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }

    public void fail() {
        this.status = NotificationStatus.FAILED;
    }
}