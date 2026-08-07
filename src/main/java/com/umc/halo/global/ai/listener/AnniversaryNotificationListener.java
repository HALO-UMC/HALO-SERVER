package com.umc.halo.global.ai.listener;

import com.umc.halo.domain.notification.entity.Anniversary;
import com.umc.halo.domain.notification.enums.NotificationType;
import com.umc.halo.domain.notification.exception.code.AnniversaryErrorCode;
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
import com.umc.halo.global.ai.service.AnniversaryNotificationWriter;
import com.umc.halo.global.ai.service.AnniversaryNotificationWriter.NotificationContent;
import com.umc.halo.global.ai.service.AnniversaryNotificationWriter.UpdateContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnniversaryNotificationListener {

    private final AnniversaryRepository anniversaryRepository;
    private final NotificationRepository notificationRepository;
    private final MemberSettingRepository memberSettingRepository;
    private final AiService aiService;
    private final AnniversaryNotificationWriter anniversaryNotificationWriter;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void generateNotificationMessage(AnniversaryCreatedEvent event) {

        Anniversary anniversary = anniversaryRepository.findById(event.anniversaryId())
                .orElseThrow(() -> new AnniversaryException(AnniversaryErrorCode.ANNIVERSARY_NOT_FOUND));
        MemberSetting memberSetting = memberSettingRepository.findByMemberId(anniversary.getMember().getId())
                .orElseThrow(() -> new SettingException(SettingErrorCode.SETTING_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        LocalTime notifyTime = memberSetting.getRegularNotificationTime();

        LocalDate nextOccurrence = resolveNextOccurrence(anniversary, now.toLocalDate());
        if (nextOccurrence == null) {
            return;
        }
        LocalDateTime d7 = nextOccurrence.minusDays(7).atTime(notifyTime);
        LocalDateTime dday = nextOccurrence.atTime(notifyTime);

        String d7Title = createNotificationTitle(anniversary, NotificationType.ANNIVERSARY_D7);
        String ddayTitle = createNotificationTitle(anniversary, NotificationType.ANNIVERSARY_DDAY);
        String d7Message = createNotificationMessage(anniversary, NotificationType.ANNIVERSARY_D7);
        String ddayMessage = createNotificationMessage(anniversary, NotificationType.ANNIVERSARY_DDAY);

        NotificationContent d7Content = (anniversary.getSevenDaysAlarmEnabled() && d7.isAfter(now))
                ? new NotificationContent(d7Title, d7Message, d7)
                : null;
        NotificationContent ddayContent = (anniversary.getDayAlarmEnabled() && dday.isAfter(now))
                ? new NotificationContent(ddayTitle, ddayMessage, dday)
                : null;

        // D7/DDay 저장을 하나의 트랜잭션으로
        anniversaryNotificationWriter.saveGenerated(event.anniversaryId(), d7Content, ddayContent);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void updateNotificationMessage(AnniversaryUpdatedEvent event) {

        Anniversary anniversary = anniversaryRepository.findById(event.anniversaryId())
                .orElseThrow(() -> new AnniversaryException(AnniversaryErrorCode.ANNIVERSARY_NOT_FOUND));
        MemberSetting memberSetting = memberSettingRepository.findByMemberId(anniversary.getMember().getId())
                .orElseThrow(() -> new SettingException(SettingErrorCode.SETTING_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        LocalTime notifyTime = memberSetting.getRegularNotificationTime();

        LocalDate nextOccurrence = resolveNextOccurrence(anniversary, now.toLocalDate());
        if (nextOccurrence == null) {
            anniversaryNotificationWriter.cancelBoth(event.anniversaryId());
            return;
        }
        LocalDateTime d7 = nextOccurrence.minusDays(7).atTime(notifyTime);
        LocalDateTime dday = nextOccurrence.atTime(notifyTime);

        String d7Title = createNotificationTitle(anniversary, NotificationType.ANNIVERSARY_D7);
        String ddayTitle = createNotificationTitle(anniversary, NotificationType.ANNIVERSARY_DDAY);
        String d7Message = null;
        String ddayMessage = null;

        if (event.titleChanged() || event.memoChanged()) {
            d7Message = createNotificationMessage(anniversary, NotificationType.ANNIVERSARY_D7);
            ddayMessage = createNotificationMessage(anniversary, NotificationType.ANNIVERSARY_DDAY);
        }

        UpdateContent d7Content = new UpdateContent(d7Title, d7Message, d7, anniversary.getSevenDaysAlarmEnabled());
        UpdateContent ddayContent = new UpdateContent(ddayTitle, ddayMessage, dday, anniversary.getDayAlarmEnabled());

        anniversaryNotificationWriter.updateGenerated(event.anniversaryId(), now, d7Content, ddayContent);
    }

    @Transactional
    public void createNextNotification(Anniversary anniversary) {

        if (!Boolean.TRUE.equals(anniversary.getIsRepeated())) {
            return;
        }

        MemberSetting memberSetting = memberSettingRepository.findByMemberId(anniversary.getMember().getId()).orElse(null);
        if (memberSetting == null) {
            return;
        }
        LocalTime notifyTime = memberSetting.getRegularNotificationTime();

        LocalDateTime now = LocalDateTime.now();
        LocalDate nextOccurrence = resolveNextOccurrence(anniversary, now.toLocalDate());
        if (nextOccurrence == null) {
            return;
        }
        LocalDateTime d7 = nextOccurrence.minusDays(7).atTime(notifyTime);
        LocalDateTime dday = nextOccurrence.atTime(notifyTime);

        if (existsNextNotification(anniversary, NotificationType.ANNIVERSARY_D7, d7) || existsNextNotification(anniversary, NotificationType.ANNIVERSARY_DDAY, dday)) {
            return;
        }

        String d7Title = createNotificationTitle(anniversary, NotificationType.ANNIVERSARY_D7);
        String ddayTitle = createNotificationTitle(anniversary, NotificationType.ANNIVERSARY_DDAY);
        String d7Message = createNotificationMessage(anniversary, NotificationType.ANNIVERSARY_D7);
        String ddayMessage = createNotificationMessage(anniversary, NotificationType.ANNIVERSARY_DDAY);

        NotificationContent d7Content = (anniversary.getSevenDaysAlarmEnabled() && d7.isAfter(now))
                ? new NotificationContent(d7Title, d7Message, d7)
                : null;
        NotificationContent ddayContent = (anniversary.getDayAlarmEnabled() && dday.isAfter(now))
                ? new NotificationContent(ddayTitle, ddayMessage, dday)
                : null;

        anniversaryNotificationWriter.saveGenerated(anniversary.getId(), d7Content, ddayContent);
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

        return "오늘의 따뜻한 안녕을 전해보세요.";
    }

    private LocalDate resolveNextOccurrence(Anniversary anniversary, LocalDate today) {

        LocalDate anniversaryDate = anniversary.getAnniversaryDate();

        if (!Boolean.TRUE.equals(anniversary.getIsRepeated())) {
            return anniversaryDate.isBefore(today) ? null : anniversaryDate;
        }

        LocalDate thisYear;

        try {
            thisYear = anniversaryDate.withYear(today.getYear());
        } catch (Exception e) {
            thisYear = LocalDate.of(today.getYear(), 2, 28);
        }

        if (thisYear.isBefore(today)) {
            try {
                return anniversaryDate.withYear(today.getYear() + 1);
            } catch (Exception e) {
                return LocalDate.of(today.getYear() + 1, 2, 28);
            }
        }

        return thisYear;
    }

    private boolean existsNextNotification(Anniversary anniversary, NotificationType notificationType, LocalDateTime scheduledAt) {
        return notificationRepository.existsByAnniversaryIdAndNotificationTypeAndScheduledAt(anniversary.getId(), notificationType, scheduledAt);
    }
}
