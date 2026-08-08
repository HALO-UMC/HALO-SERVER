package com.umc.halo.domain.notification.service;

import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.exception.MemberException;
import com.umc.halo.domain.member.exception.code.MemberErrorCode;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.notification.converter.NotificationConverter;
import com.umc.halo.domain.notification.entity.Anniversary;
import com.umc.halo.domain.notification.entity.CommonAnniversary;
import com.umc.halo.domain.notification.entity.Notification;
import com.umc.halo.domain.notification.enums.NotificationStatus;
import com.umc.halo.domain.notification.enums.NotificationType;
import com.umc.halo.domain.notification.repository.NotificationRepository;
import com.umc.halo.domain.setting.entity.MemberSetting;
import com.umc.halo.domain.setting.exception.SettingException;
import com.umc.halo.domain.setting.exception.code.SettingErrorCode;
import com.umc.halo.domain.setting.repository.MemberSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationTransactionService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final MemberSettingRepository memberSettingRepository;

    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");

    @Transactional
    public List<Notification> claimNotifications() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime timeOut = now.minusMinutes(10);

        List<Notification> notifications = notificationRepository.findTargetsForUpdate(NotificationStatus.SCHEDULED, NotificationStatus.PROCESSING, now, timeOut);

        notifications.forEach(Notification::startProcessing);

        return notifications;
    }

    @Transactional
    public void createRetentionNotification(Long memberId) {

        Member member = memberRepository.findByIdForUpdate(memberId).orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        MemberSetting memberSetting = memberSettingRepository.findByMemberId(memberId).orElseThrow(() -> new SettingException(SettingErrorCode.SETTING_NOT_FOUND));

        LocalDate today = LocalDate.now(ZONE_ID);
        LocalDateTime scheduledAt = today.atTime(9, 0);

        LocalDateTime lastAccessAt = member.getLastAccessAt();

        if (lastAccessAt == null) {
            return;
        }

        long inactiveDays = ChronoUnit.DAYS.between(lastAccessAt.toLocalDate(), today);

        if (!shouldCreateRetentionNotification(inactiveDays)) {
            return;
        }

        String title = resolveRetentionTitle(inactiveDays);
        String message = resolveRetentionMessage(inactiveDays);

        boolean exists = notificationRepository.existsByMemberAndNotificationTypeAndScheduledAt(member, NotificationType.RETENTION, scheduledAt);

        if (exists) {
            return;
        }

        List<Notification> notifications = notificationRepository.findByMemberIdAndNotificationTypeAndStatusIn(member.getId(), NotificationType.RETENTION, List.of(NotificationStatus.SCHEDULED));

        notifications.forEach(Notification::expire);

        Notification notification = NotificationConverter.toRetentionNotification(member, title, message, scheduledAt, memberSetting.getRetentionNotificationEnabled());

        notificationRepository.save(notification);
    }

    @Transactional
    public void createCommonAnniversaryNotification(MemberSetting memberSetting, List<CommonAnniversary> commonAnniversaries, LocalDate today) {

        Member member = memberSetting.getMember();

        LocalTime notifyTime = memberSetting.getRegularNotificationTime();
        if (notifyTime == null) {
            return;
        }
        LocalDateTime scheduledAt = today.atTime(notifyTime);

        for (CommonAnniversary commonAnniversary : commonAnniversaries) {

            boolean exists = notificationRepository.existsByMemberAndNotificationTypeAndScheduledAt(member, NotificationType.COMMON_ANNIVERSARY, scheduledAt);

            if (exists) {
                continue;
            }

            Notification notification = NotificationConverter.toCommonAnniversaryNotification(member, commonAnniversary.getTitle(), commonAnniversary.getMemo(), scheduledAt, memberSetting.getAnniversaryNotificationEnabled());

            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void saveOrUpdateNotification(Anniversary anniversary, MemberSetting memberSetting, NotificationType notificationType, String title, String message, LocalDateTime scheduledAt) {

        Notification notification = notificationRepository.findByAnniversaryIdAndNotificationTypeAndStatusIn(anniversary.getId(), notificationType, List.of(NotificationStatus.SCHEDULED, NotificationStatus.EXPIRED)).orElse(null);

        boolean anniversaryEnabled = notificationType == NotificationType.ANNIVERSARY_D7 ? anniversary.getSevenDaysAlarmEnabled() : anniversary.getDayAlarmEnabled();

        if(notification == null) {
            notificationRepository.insertAnniversaryNotificationIfAbsent(anniversary.getId(), anniversary.getMember().getId(), notificationType, title, message, scheduledAt, memberSetting.getAnniversaryNotificationEnabled(), anniversaryEnabled);
            return;
        }

        notification.updateSettingEnabled(memberSetting.getAnniversaryNotificationEnabled());
        notification.updateAnniversaryEnabled(anniversaryEnabled);
        notification.updateContent(title, message);
        notification.reserve(scheduledAt);
    }

    @Transactional
    public void updateOrCancelNotification(Anniversary anniversary, MemberSetting memberSetting, NotificationType notificationType, String title, String message, LocalDateTime scheduledAt, LocalDateTime now, boolean enabled) {

        Notification notification = notificationRepository.findByAnniversaryIdAndNotificationTypeAndStatusIn(anniversary.getId(), notificationType, List.of(NotificationStatus.SCHEDULED, NotificationStatus.EXPIRED)).orElse(null);

        if (notification == null) {
            if (!enabled || !scheduledAt.isAfter(now)) {
                return;
            }
            notificationRepository.save(NotificationConverter.toAnniversaryNotification(anniversary, notificationType, title, message != null ? message : createDefaultNotificationMessage(notificationType), scheduledAt, memberSetting.getAnniversaryNotificationEnabled(), enabled));
            return;
        }

        notification.updateSettingEnabled(memberSetting.getAnniversaryNotificationEnabled());
        notification.updateAnniversaryEnabled(enabled);

        if (!scheduledAt.isAfter(now)) {
            notification.expire();
            return;
        }

        if (message != null) {
            notification.updateContent(title, message);
        }

        notification.reserve(scheduledAt);
    }

    @Transactional
    public void cancelNotification(Anniversary anniversary, NotificationType notificationType) {

        Notification notification = notificationRepository.findByAnniversaryIdAndNotificationTypeAndStatusIn(anniversary.getId(), notificationType, List.of(NotificationStatus.SCHEDULED, NotificationStatus.EXPIRED)).orElse(null);

        if(notification != null) {
            notification.updateAnniversaryEnabled(false);
        }
    }

    @Transactional
    public void completeSend(Long notificationId, UUID leaseId){
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        notification.send(leaseId);
    }


    @Transactional
    public void failSend(Long notificationId, UUID leaseId){
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        notification.fail(leaseId);
    }

    @Transactional
    public void expireSend(Long notificationId, UUID leaseId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        notification.expireWithLease(leaseId);
    }

    private boolean shouldCreateRetentionNotification(long inactiveDays) {
        return inactiveDays >= 1 &&
                (inactiveDays <= 13 || inactiveDays % 7 == 0);
    }

    private String resolveRetentionTitle(long inactiveDays) {
        if (inactiveDays >= 14 && inactiveDays % 7 == 0) {
            return "부모님과의 이야기는 언제든 이어갈 수 있어요.";
        }
        if (inactiveDays >= 7) {
            return "다시 시작해 볼까요?";
        }
        if (inactiveDays >= 3) {
            return "부모님과의 이야기가 잠시 멈춰있어요.";
        }
        return "오늘의 이야기가 기다리고 있어요.";
    }

    private String resolveRetentionMessage(long inactiveDays) {
        if (inactiveDays >= 14 && inactiveDays % 7 == 0) {
            return "오늘 한 장으로 다시 시작해 보세요.";
        }
        if (inactiveDays >= 7) {
            return "오늘 한 장이면 충분해요.";
        }
        if (inactiveDays >= 3) {
            return "오늘 한 장으로 다시 이어가 보세요.";
        }
        return "오늘도 부모님과의 이야기를 이어가 볼까요?";
    }

    private String createDefaultNotificationMessage(NotificationType notificationType) {

        if (notificationType == NotificationType.ANNIVERSARY_D7) {
            return "오늘부터 조금씩 마음을 준비해 보세요.";
        }

        return "오늘의 따뜻한 안녕을 전해보세요.";
    }
}
