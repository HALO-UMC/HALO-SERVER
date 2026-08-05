package com.umc.halo.domain.notification.service;

import com.umc.halo.domain.member.entity.MemberDevice;
import com.umc.halo.domain.member.repository.MemberDeviceRepository;
import com.umc.halo.domain.notification.entity.Notification;
import com.umc.halo.domain.setting.entity.MemberSetting;
import com.umc.halo.domain.setting.exception.SettingException;
import com.umc.halo.domain.setting.exception.code.SettingErrorCode;
import com.umc.halo.domain.setting.repository.MemberSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationTransactionService notificationTransactionService;
    private final MemberDeviceRepository memberDeviceRepository;
    private final MemberSettingRepository memberSettingRepository;
    private final FcmService fcmService;

    public void sendScheduledNotifications() {

        List<Notification> notifications = notificationTransactionService.claimNotifications();

        for (Notification notification : notifications) {

            UUID leaseId = notification.getProcessingLeaseId();

            if (leaseId == null) {
                log.warn("processing lease가 없는 notificationId={}", notification.getId());
                continue;
            }

            if (!canSend(notification)) {
                notificationTransactionService.expireSend(notification.getId(), leaseId);
                continue;
            }

            try {
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
