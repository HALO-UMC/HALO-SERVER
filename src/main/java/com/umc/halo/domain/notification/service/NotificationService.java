package com.umc.halo.domain.notification.service;

import com.umc.halo.domain.member.entity.MemberDevice;
import com.umc.halo.domain.member.repository.MemberDeviceRepository;
import com.umc.halo.domain.notification.entity.Notification;
import com.umc.halo.domain.notification.enums.NotificationStatus;
import com.umc.halo.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberDeviceRepository memberDeviceRepository;
    private final FcmService fcmService;

    public void sendScheduledNotifications() {

        List<Notification> notifications = claimNotifications();

        for (Notification notification : notifications) {

            if (!canSend(notification)) {
                resetToScheduled(notification.getId());
                continue;
            }

            try {
                boolean success = send(notification);

                if (success) {
                    completeSend(notification.getId());
                } else {
                    failSend(notification.getId());
                }

            } catch (Exception e) {
                failSend(notification.getId());
            }
        }
    }

    @Transactional
    public List<Notification> claimNotifications() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime timeOut = now.minusMinutes(10);

        List<Notification> notifications = notificationRepository.findTargetsForUpdate(NotificationStatus.SCHEDULED, NotificationStatus.PROCESSING, now, timeOut);

        notifications.forEach(Notification::startProcessing);

        return notifications;
    }

    private boolean canSend(Notification notification) {
        return switch (notification.getNotificationType()) {
            case ANNIVERSARY_D7, ANNIVERSARY_DDAY -> Boolean.TRUE.equals(notification.getAnniversaryEnabled()) && Boolean.TRUE.equals(notification.getSettingEnabled());
            case TODAY_CHAPTER, RETENTION -> Boolean.TRUE.equals(notification.getSettingEnabled());
        };
    }

    private boolean send(Notification notification) {
        List<MemberDevice> memberDevices = memberDeviceRepository.findAllByMember(notification.getMember());

        int successCount = 0;

        for (MemberDevice memberDevice : memberDevices) {
            try {
                fcmService.send(memberDevice.getFcmToken(), notification.getTitle(), notification.getMessage());
                successCount++;
            } catch (Exception e) {
                log.error("FCM 전송 실패 memberId={}, deviceId={}, notificationId={}", notification.getMember().getId(), memberDevice.getId(), notification.getId(), e);
            }
        }

        return successCount > 0;
    }

    @Transactional
    public void completeSend(Long notificationId){
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        notification.send();
    }


    @Transactional
    public void failSend(Long notificationId){
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        notification.fail();
    }

    @Transactional
    public void resetToScheduled(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        notification.resetToScheduled();
    }
}
