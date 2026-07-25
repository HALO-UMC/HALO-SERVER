package com.umc.halo.global.ai.listener;

import com.umc.halo.domain.notification.converter.NotificationConverter;
import com.umc.halo.domain.notification.entity.Anniversary;
import com.umc.halo.domain.notification.entity.Notification;
import com.umc.halo.domain.notification.enums.NotificationStatus;
import com.umc.halo.domain.notification.enums.NotificationType;
import com.umc.halo.domain.notification.exception.AnniversaryErrorCode;
import com.umc.halo.domain.notification.exception.AnniversaryException;
import com.umc.halo.domain.notification.repository.AnniversaryRepository;
import com.umc.halo.domain.notification.repository.NotificationRepository;
import com.umc.halo.domain.setting.entity.MemberSetting;
import com.umc.halo.domain.setting.exception.SettingException;
import com.umc.halo.domain.setting.exception.code.SettingErrorCode;
import com.umc.halo.domain.setting.repository.MemberSettingRepository;
import com.umc.halo.global.ai.event.AnniversaryCreatedEvent;
import com.umc.halo.global.ai.event.AnniversaryUpdatedEvent;
import com.umc.halo.global.ai.exception.AiException;
import com.umc.halo.global.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnniversaryNotificationListener {

    private final AnniversaryRepository anniversaryRepository;
    private final NotificationRepository notificationRepository;
    private final MemberSettingRepository memberSettingRepository;
    private final AiService aiService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generateNotificationMessage(AnniversaryCreatedEvent event) {

        Anniversary anniversary = anniversaryRepository.findById(event.anniversaryId())
                .orElseThrow(() -> new AnniversaryException(AnniversaryErrorCode.ANNIVERSARY_NOT_FOUND));
        MemberSetting memberSetting = memberSettingRepository.findByMemberId(anniversary.getMember().getId())
                .orElseThrow(() -> new SettingException(SettingErrorCode.SETTING_NOT_FOUND));

        String d7Title = createNotificationTitle(anniversary, NotificationType.ANNIVERSARY_D7);
        String ddayTitle = createNotificationTitle(anniversary, NotificationType.ANNIVERSARY_DDAY);
        String d7Message = createNotificationMessage(anniversary, NotificationType.ANNIVERSARY_D7);
        String ddayMessage = createNotificationMessage(anniversary, NotificationType.ANNIVERSARY_DDAY);

        LocalDateTime now = LocalDateTime.now();
        LocalTime notifyTime = memberSetting.getRegularNotificationTime();
        LocalDateTime d7 = anniversary.getAnniversaryDate().minusDays(7).atTime(notifyTime);
        LocalDateTime dday = anniversary.getAnniversaryDate().atTime(notifyTime);


        if (anniversary.getSevenDaysAlarmEnabled() && d7.isAfter(now)) {
            saveOrUpdateNotification(anniversary, NotificationType.ANNIVERSARY_D7, d7Title, d7Message, d7);
        }

        if (anniversary.getDayAlarmEnabled() && dday.isAfter(now)) {
            saveOrUpdateNotification(anniversary, NotificationType.ANNIVERSARY_DDAY, ddayTitle, ddayMessage, dday);
        }

    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateNotificationMessage(AnniversaryUpdatedEvent event) {

        Anniversary anniversary = anniversaryRepository.findById(event.anniversaryId())
                .orElseThrow(() -> new AnniversaryException(AnniversaryErrorCode.ANNIVERSARY_NOT_FOUND));
        MemberSetting memberSetting = memberSettingRepository.findByMemberId(anniversary.getMember().getId())
                .orElseThrow(() -> new SettingException(SettingErrorCode.SETTING_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        LocalTime notifyTime = memberSetting.getRegularNotificationTime();
        LocalDateTime d7 = anniversary.getAnniversaryDate().minusDays(7).atTime(notifyTime);
        LocalDateTime dday = anniversary.getAnniversaryDate().atTime(notifyTime);

        String d7Title = createNotificationTitle(anniversary, NotificationType.ANNIVERSARY_D7);
        String ddayTitle = createNotificationTitle(anniversary, NotificationType.ANNIVERSARY_DDAY);
        String d7Message = null;
        String ddayMessage = null;

        if(event.titleChanged() || event.memoChanged()) {
            d7Message = createNotificationMessage(anniversary, NotificationType.ANNIVERSARY_D7);
            ddayMessage = createNotificationMessage(anniversary, NotificationType.ANNIVERSARY_DDAY);
        }

        updateOrCancel(anniversary, NotificationType.ANNIVERSARY_D7, d7Title, d7Message, d7, now, anniversary.getSevenDaysAlarmEnabled());;
        updateOrCancel(anniversary, NotificationType.ANNIVERSARY_DDAY, ddayTitle, ddayMessage, dday, now, anniversary.getDayAlarmEnabled());

    }

    private String createNotificationTitle(Anniversary anniversary, NotificationType notificationType) {
        if (notificationType == NotificationType.ANNIVERSARY_D7) {
            return anniversary.getTitle() + "까지 7일 남았어요.";
        }

        return "오늘은 " + anniversary.getTitle() + "입니다.";
    }

    private String createNotificationMessage(Anniversary anniversary, NotificationType notificationType) {

        if (anniversary.getMemo() == null || anniversary.getMemo().isBlank()) {
            return createDefaultNotificationMessage(notificationType);
        }

        try {
            return aiService.generateAnniversaryNotificationMessage(anniversary.getTitle(), anniversary.getMemo());
        } catch (AiException e) {
            log.warn("AI 알림 문구 생성 실패. anniversaryId={}", anniversary.getId(), e);
            return createDefaultNotificationMessage(notificationType);
        }

    }

    private String createDefaultNotificationMessage(NotificationType notificationType) {

        if (notificationType == NotificationType.ANNIVERSARY_D7) {
            return "오늘부터 조금씩 마음을 준비해 보세요.";
        }

        return "소중한 마음을 전하는 하루가 되어보세요.";
    }



    private void saveOrUpdateNotification(Anniversary anniversary, NotificationType notificationType, String title, String message, LocalDateTime scheduledAt) {

        Notification notification = notificationRepository.findByAnniversaryIdAndNotificationTypeAndStatusIn(anniversary.getId(), notificationType, List.of(NotificationStatus.SCHEDULED, NotificationStatus.CANCELED)).orElse(null);

        if(notification == null) {
            notificationRepository.save(NotificationConverter.toAnniversaryNotification(anniversary, notificationType, title, message, scheduledAt));
        } else {
            notification.update(title, message, scheduledAt);
        }
    }

    private void updateOrCancel(Anniversary anniversary, NotificationType type, String title, String message, LocalDateTime scheduledAt, LocalDateTime now, boolean enabled) {

        Notification notification = notificationRepository.findByAnniversaryIdAndNotificationTypeAndStatusIn(anniversary.getId(), type, List.of(NotificationStatus.SCHEDULED, NotificationStatus.CANCELED)).orElse(null);

        if(enabled && scheduledAt.isAfter(now)) {
            if(notification == null) {
                notificationRepository.save(NotificationConverter.toAnniversaryNotification(anniversary, type, title, message != null ? message : createDefaultNotificationMessage(type), scheduledAt));
            } else {
                if(message != null) {
                    notification.update(title, message, scheduledAt);
                } else {
                    notification.reserve(scheduledAt);
                }
            }
        } else {
            if(notification != null) {
                notification.cancel();
            }
        }
    }
}
