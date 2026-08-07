package com.umc.halo.domain.notification.scheduler;

import com.umc.halo.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationService notificationService;

    @Scheduled(cron = "0 * * * * *")
    public void sendScheduledNotification() {
        notificationService.sendScheduledNotifications();
    }

    @Scheduled(cron = "0 5 0 * * *")
    public void createNextYearNotifications() {
        notificationService.createNextYearNotifications();
    }

    @Scheduled(cron = "0 59 23 * * *", zone = "Asia/Seoul")
    public void createTodayChapterNotifications() {
        notificationService.createTodayChapterNotifications();
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void createRetentionNotifications() {
        notificationService.createRetentionNotifications();
    }
}
