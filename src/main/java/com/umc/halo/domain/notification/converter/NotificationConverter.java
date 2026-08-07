package com.umc.halo.domain.notification.converter;

import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.notification.entity.Anniversary;
import com.umc.halo.domain.notification.entity.Notification;
import com.umc.halo.domain.notification.enums.NotificationStatus;
import com.umc.halo.domain.notification.enums.NotificationType;

import java.time.LocalDateTime;

public class NotificationConverter {

    private NotificationConverter() {
        throw new IllegalStateException("Utility class");
    }

    public static Notification toAnniversaryNotification(Anniversary anniversary, NotificationType notificationType, String title, String message, LocalDateTime scheduledAt, Boolean settingEnabled, Boolean anniversaryEnabled) {
        return Notification.builder()
                .member(anniversary.getMember())
                .anniversary(anniversary)
                .notificationType(notificationType)
                .title(title)
                .message(message)
                .scheduledAt(scheduledAt)
                .status(NotificationStatus.SCHEDULED)
                .settingEnabled(settingEnabled)
                .anniversaryEnabled(anniversaryEnabled)
                .build();
    }

    public static Notification toTodayChapterNotification(Member member, String title, String message, LocalDateTime scheduledAt, Boolean settingEnabled) {
        return Notification.builder()
                .member(member)
                .notificationType(NotificationType.TODAY_CHAPTER)
                .title(title)
                .message(message)
                .scheduledAt(scheduledAt)
                .status(NotificationStatus.SCHEDULED)
                .settingEnabled(settingEnabled)
                .build();
    }

    public static Notification toRetentionNotification(Member member, String title, String message, LocalDateTime scheduledAt, Boolean settingEnabled) {
        return Notification.builder()
                .member(member)
                .notificationType(NotificationType.RETENTION)
                .title(title)
                .message(message)
                .scheduledAt(scheduledAt)
                .status(NotificationStatus.SCHEDULED)
                .settingEnabled(settingEnabled)
                .build();
    }
}
