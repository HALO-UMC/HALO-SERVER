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
        uniqueConstraints = @UniqueConstraint(columnNames = {"anniversary_id", "notification_type"})
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
    @Column(name = "status")
    @Builder.Default
    private NotificationStatus status = NotificationStatus.SCHEDULED;

    public void update(String title, String message, LocalDateTime scheduledAt) {
        this.title = title;
        this.message = message;
        this.scheduledAt = scheduledAt;
        if (this.status != NotificationStatus.SENT) {
            this.status = NotificationStatus.SCHEDULED;
        }
    }

    public void reserve(LocalDateTime scheduledAt) {
        if (this.status == NotificationStatus.SENT) {
            return;
        }
        this.scheduledAt = scheduledAt;
        this.status = NotificationStatus.SCHEDULED;
    }

    public void cancel() {
        if (this.status != NotificationStatus.SENT) {
            this.status = NotificationStatus.CANCELED;
        }
    }

    public boolean isReserved() {
        return status == NotificationStatus.SCHEDULED;
    }

    public boolean isCanceled() {
        return status == NotificationStatus.CANCELED;
    }
}