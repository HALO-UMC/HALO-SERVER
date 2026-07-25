package com.umc.halo.domain.notification.converter;

import com.umc.halo.domain.notification.entity.Anniversary;
import com.umc.halo.domain.notification.entity.Notification;
import com.umc.halo.domain.notification.enums.NotificationType;

import java.time.LocalDateTime;

public class NotificationConverter {

    private NotificationConverter() {
        throw new IllegalStateException("Utility class");
    }

    public static Notification toAnniversaryNotification(Anniversary anniversary, NotificationType notificationType, String title, String message, LocalDateTime scheduledAt) {
        return Notification.builder()
                .member(anniversary.getMember())
                .anniversary(anniversary)
                .notificationType(notificationType)
                .title(title)
                .message(message)
                .scheduledAt(scheduledAt)
                .build();
    }
}
