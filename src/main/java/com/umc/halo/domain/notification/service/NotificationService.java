package com.umc.halo.domain.notification.service;

import com.umc.halo.domain.member.entity.MemberDevice;
import com.umc.halo.domain.member.repository.MemberDeviceRepository;
import com.umc.halo.domain.notification.entity.Anniversary;
import com.umc.halo.domain.notification.entity.Notification;
import com.umc.halo.domain.notification.enums.NotificationStatus;
import com.umc.halo.domain.notification.enums.NotificationType;
import com.umc.halo.domain.notification.repository.NotificationRepository;
import com.umc.halo.domain.setting.entity.MemberSetting;
import com.umc.halo.domain.setting.exception.SettingException;
import com.umc.halo.domain.setting.exception.code.SettingErrorCode;
import com.umc.halo.domain.setting.repository.MemberSettingRepository;
import com.umc.halo.global.ai.listener.AnniversaryNotificationListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationTransactionService notificationTransactionService;
    private final MemberDeviceRepository memberDeviceRepository;
    private final MemberSettingRepository memberSettingRepository;
    private final NotificationRepository notificationRepository;
    private final FcmService fcmService;
    private final AnniversaryNotificationListener anniversaryNotificationListener;

    public void sendScheduledNotifications() {

        List<Notification> notifications = notificationTransactionService.claimNotifications();

        for (Notification notification : notifications) {

            UUID leaseId = notification.getProcessingLeaseId();

            if (leaseId == null) {
                log.warn("processing lease가 없는 notificationId={}", notification.getId());
                continue;
            }

            try {

                if (!canSend(notification)) {
                    notificationTransactionService.expireSend(notification.getId(), leaseId);
                    continue;
                }

                boolean success = send(notification);

                if (success) {
                    notificationTransactionService.completeSend(notification.getId(), leaseId);
                } else {
                    notificationTransactionService.failSend(notification.getId(), leaseId);
                }

            } catch (Exception e) {
                notificationTransactionService.failSend(notification.getId(), leaseId);
            }
        }
    }

    @Transactional
    public void createNextYearNotifications() {

        List<Anniversary> anniversaries = notificationRepository.findAllRepeatedAnniversaries();

        for (Anniversary anniversary : anniversaries) {
            anniversaryNotificationListener.createNextNotification(anniversary);
        }
    }

    private boolean canSend(Notification notification) {
        MemberSetting memberSetting = memberSettingRepository.findByMemberId(notification.getMember().getId()).orElseThrow(() -> new SettingException(SettingErrorCode.SETTING_NOT_FOUND));

        if (!Boolean.TRUE.equals(memberSetting.getIsAllNotificationEnabled())) {
            return false;
        }

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
}
