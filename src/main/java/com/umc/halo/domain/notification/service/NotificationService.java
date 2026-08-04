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

    @Transactional
    public void sendScheduledNotification() {

        LocalDateTime now = LocalDateTime.now();

        List<Notification> notifications = notificationRepository.findAllByStatusAndScheduledAtLessThanEqual(NotificationStatus.SCHEDULED, now);

        for (Notification notification : notifications) {
            if (!canSend(notification)) {
                continue;
            }

            send(notification);
        }
    }

    private boolean canSend(Notification notification) {
        return switch (notification.getNotificationType()) {
            case ANNIVERSARY_D7, ANNIVERSARY_DDAY -> notification.getAnniversaryEnabled() && notification.getSettingEnabled();
            case TODAY_CHAPTER -> notification.getSettingEnabled();
            case RETENTION -> notification.getSettingEnabled();
        };
    }

    private void send(Notification notification) {
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

        if (successCount > 0) {
            notification.send();
        } else {
            notification.fail();
        }
    }
}
