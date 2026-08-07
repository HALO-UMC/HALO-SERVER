package com.umc.halo.global.ai.service;

import com.umc.halo.domain.notification.converter.NotificationConverter;
import com.umc.halo.domain.notification.entity.Anniversary;
import com.umc.halo.domain.notification.entity.Notification;
import com.umc.halo.domain.notification.enums.NotificationStatus;
import com.umc.halo.domain.notification.enums.NotificationType;
import com.umc.halo.domain.notification.exception.AnniversaryException;
import com.umc.halo.domain.notification.exception.code.AnniversaryErrorCode;
import com.umc.halo.domain.notification.repository.AnniversaryRepository;
import com.umc.halo.domain.notification.repository.NotificationRepository;
import com.umc.halo.domain.setting.entity.MemberSetting;
import com.umc.halo.domain.setting.exception.SettingException;
import com.umc.halo.domain.setting.exception.code.SettingErrorCode;
import com.umc.halo.domain.setting.repository.MemberSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// AI 문구 생성이 끝난 뒤(AnniversaryNotificationListener) DB 작성
@Component
@RequiredArgsConstructor
public class AnniversaryNotificationWriter {

    private final AnniversaryRepository anniversaryRepository;
    private final MemberSettingRepository memberSettingRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public void saveGenerated(Long anniversaryId, NotificationContent d7, NotificationContent dday) {
        Anniversary anniversary = findAnniversary(anniversaryId);
        MemberSetting memberSetting = findMemberSetting(anniversary);

        if (d7 != null) {
            saveOrUpdateNotification(anniversary, memberSetting, NotificationType.ANNIVERSARY_D7, d7.title(), d7.message(), d7.scheduledAt());
        }
        if (dday != null) {
            saveOrUpdateNotification(anniversary, memberSetting, NotificationType.ANNIVERSARY_DDAY, dday.title(), dday.message(), dday.scheduledAt());
        }
    }

    @Transactional
    public void updateGenerated(Long anniversaryId, LocalDateTime now, UpdateContent d7, UpdateContent dday) {
        Anniversary anniversary = findAnniversary(anniversaryId);
        MemberSetting memberSetting = findMemberSetting(anniversary);

        updateOrCancelNotification(anniversary, memberSetting, NotificationType.ANNIVERSARY_D7,
                d7.title(), d7.message(), d7.scheduledAt(), now, d7.enabled());
        updateOrCancelNotification(anniversary, memberSetting, NotificationType.ANNIVERSARY_DDAY,
                dday.title(), dday.message(), dday.scheduledAt(), now, dday.enabled());
    }

    @Transactional
    public void cancelBoth(Long anniversaryId) {
        Anniversary anniversary = findAnniversary(anniversaryId);
        cancelNotification(anniversary, NotificationType.ANNIVERSARY_D7);
        cancelNotification(anniversary, NotificationType.ANNIVERSARY_DDAY);
    }

    private Anniversary findAnniversary(Long anniversaryId) {
        return anniversaryRepository.findById(anniversaryId)
                .orElseThrow(() -> new AnniversaryException(AnniversaryErrorCode.ANNIVERSARY_NOT_FOUND));
    }

    private MemberSetting findMemberSetting(Anniversary anniversary) {
        return memberSettingRepository.findByMemberId(anniversary.getMember().getId())
                .orElseThrow(() -> new SettingException(SettingErrorCode.SETTING_NOT_FOUND));
    }

    private void saveOrUpdateNotification(Anniversary anniversary, MemberSetting memberSetting, NotificationType notificationType, String title, String message, LocalDateTime scheduledAt) {

        Notification notification = notificationRepository.findByAnniversaryIdAndNotificationTypeAndStatusIn(anniversary.getId(), notificationType, List.of(NotificationStatus.SCHEDULED, NotificationStatus.EXPIRED)).orElse(null);

        boolean anniversaryEnabled = notificationType == NotificationType.ANNIVERSARY_D7 ? anniversary.getSevenDaysAlarmEnabled() : anniversary.getDayAlarmEnabled();

        if (notification == null) {
            notificationRepository.save(NotificationConverter.toAnniversaryNotification(anniversary, notificationType, title, message, scheduledAt, memberSetting.getAnniversaryNotificationEnabled(), anniversaryEnabled));
            return;
        }

        notification.updateSettingEnabled(memberSetting.getAnniversaryNotificationEnabled());
        notification.updateAnniversaryEnabled(anniversaryEnabled);
        notification.updateContent(title, message);
        notification.reserve(scheduledAt);
        notificationRepository.save(notification);
    }

    private void updateOrCancelNotification(Anniversary anniversary, MemberSetting memberSetting, NotificationType notificationType, String title, String message, LocalDateTime scheduledAt, LocalDateTime now, boolean enabled) {

        Notification notification = notificationRepository.findByAnniversaryIdAndNotificationTypeAndStatusIn(anniversary.getId(), notificationType, List.of(NotificationStatus.SCHEDULED, NotificationStatus.EXPIRED)).orElse(null);

        if (notification == null) {
            if (!enabled || !scheduledAt.isAfter(now)) {
                return;
            }
            notificationRepository.save(NotificationConverter.toAnniversaryNotification(anniversary, notificationType, title, message != null ? message : defaultMessage(notificationType), scheduledAt, memberSetting.getAnniversaryNotificationEnabled(), enabled));
            return;
        }

        notification.updateSettingEnabled(memberSetting.getAnniversaryNotificationEnabled());
        notification.updateAnniversaryEnabled(enabled);

        if (!scheduledAt.isAfter(now)) {
            notification.expire();
            notificationRepository.save(notification);
            return;
        }

        if (message != null) {
            notification.updateContent(title, message);
        }

        notification.reserve(scheduledAt);
        notificationRepository.save(notification);
    }

    private void cancelNotification(Anniversary anniversary, NotificationType notificationType) {

        Notification notification = notificationRepository.findByAnniversaryIdAndNotificationTypeAndStatusIn(anniversary.getId(), notificationType, List.of(NotificationStatus.SCHEDULED, NotificationStatus.EXPIRED)).orElse(null);

        if (notification != null) {
            notification.updateAnniversaryEnabled(false);
            notificationRepository.save(notification);
        }
    }

    private String defaultMessage(NotificationType notificationType) {
        if (notificationType == NotificationType.ANNIVERSARY_D7) {
            return "오늘부터 조금씩 마음을 준비해 보세요.";
        }
        return "오늘의 따뜻한 안녕을 전해보세요.";
    }

    public record NotificationContent(String title, String message, LocalDateTime scheduledAt) {
    }

    public record UpdateContent(String title, String message, LocalDateTime scheduledAt, boolean enabled) {
    }
}