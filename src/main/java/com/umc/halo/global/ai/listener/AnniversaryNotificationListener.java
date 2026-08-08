package com.umc.halo.global.ai.listener;

import com.umc.halo.domain.notification.entity.Anniversary;
import com.umc.halo.domain.notification.enums.NotificationType;
import com.umc.halo.domain.notification.exception.code.AnniversaryErrorCode;
import com.umc.halo.domain.notification.exception.AnniversaryException;
import com.umc.halo.domain.notification.repository.AnniversaryRepository;
import com.umc.halo.domain.notification.service.NotificationTransactionService;
import com.umc.halo.domain.setting.entity.MemberSetting;
import com.umc.halo.domain.setting.exception.SettingException;
import com.umc.halo.domain.setting.exception.code.SettingErrorCode;
import com.umc.halo.domain.setting.repository.MemberSettingRepository;
import com.umc.halo.global.ai.event.AnniversaryCreatedEvent;
import com.umc.halo.global.ai.event.AnniversaryUpdatedEvent;
import com.umc.halo.global.ai.event.CreateNextYearNotificationEvent;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnniversaryNotificationListener {

    private final AnniversaryRepository anniversaryRepository;
    private final MemberSettingRepository memberSettingRepository;
    private final AiService aiService;
    private final NotificationTransactionService notificationTransactionService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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


        if (anniversary.getSevenDaysAlarmEnabled() && d7.isAfter(now)) {
            notificationTransactionService.saveOrUpdateNotification(anniversary, memberSetting, NotificationType.ANNIVERSARY_D7, d7Title, d7Message, d7);
        }

        if (anniversary.getDayAlarmEnabled() && dday.isAfter(now)) {
            notificationTransactionService.saveOrUpdateNotification(anniversary, memberSetting, NotificationType.ANNIVERSARY_DDAY, ddayTitle, ddayMessage, dday);
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

        LocalDate nextOccurrence = resolveNextOccurrence(anniversary, now.toLocalDate());
        if (nextOccurrence == null) {
            notificationTransactionService.cancelNotification(anniversary, NotificationType.ANNIVERSARY_D7);
            notificationTransactionService.cancelNotification(anniversary, NotificationType.ANNIVERSARY_DDAY);
            return;
        }
        LocalDateTime d7 = nextOccurrence.minusDays(7).atTime(notifyTime);
        LocalDateTime dday = nextOccurrence.atTime(notifyTime);

        String d7Title = createNotificationTitle(anniversary, NotificationType.ANNIVERSARY_D7);
        String ddayTitle = createNotificationTitle(anniversary, NotificationType.ANNIVERSARY_DDAY);
        String d7Message = null;
        String ddayMessage = null;

        if(event.titleChanged() || event.memoChanged()) {
            d7Message = createNotificationMessage(anniversary, NotificationType.ANNIVERSARY_D7);
            ddayMessage = createNotificationMessage(anniversary, NotificationType.ANNIVERSARY_DDAY);
        }

        notificationTransactionService.updateOrCancelNotification(anniversary, memberSetting, NotificationType.ANNIVERSARY_D7, d7Title, d7Message, d7, now, anniversary.getSevenDaysAlarmEnabled());
        notificationTransactionService.updateOrCancelNotification(anniversary, memberSetting, NotificationType.ANNIVERSARY_DDAY, ddayTitle, ddayMessage, dday, now, anniversary.getDayAlarmEnabled());

    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void createNextNotification(CreateNextYearNotificationEvent event) {

        Anniversary anniversary = anniversaryRepository.findById(event.anniversaryId())
                .orElseThrow(() -> new AnniversaryException(AnniversaryErrorCode.ANNIVERSARY_NOT_FOUND));

        if (!Boolean.TRUE.equals(anniversary.getIsRepeated())) {
            return;
        }

        Long memberId = anniversaryRepository.findMemberIdById(anniversary.getId()).orElseThrow(() -> new AnniversaryException(AnniversaryErrorCode.ANNIVERSARY_NOT_FOUND));

        MemberSetting memberSetting = memberSettingRepository.findByMemberId(memberId).orElse(null);
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

        String d7Title = createNotificationTitle(anniversary, NotificationType.ANNIVERSARY_D7);
        String ddayTitle = createNotificationTitle(anniversary, NotificationType.ANNIVERSARY_DDAY);

        if (anniversary.getSevenDaysAlarmEnabled() && d7.isAfter(now)) {
            String d7Message = createNotificationMessage(anniversary, NotificationType.ANNIVERSARY_D7);
            notificationTransactionService.saveOrUpdateNotification(anniversary, memberSetting, NotificationType.ANNIVERSARY_D7, d7Title, d7Message, d7);
        }

        if (anniversary.getDayAlarmEnabled() && dday.isAfter(now)) {
            String ddayMessage = createNotificationMessage(anniversary, NotificationType.ANNIVERSARY_DDAY);
            notificationTransactionService.saveOrUpdateNotification(anniversary, memberSetting, NotificationType.ANNIVERSARY_DDAY, ddayTitle, ddayMessage, dday);
        }
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
}
