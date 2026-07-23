package com.umc.halo.global.ai.listener;

import com.umc.halo.domain.notification.converter.NotificationConverter;
import com.umc.halo.domain.notification.entity.Anniversary;
import com.umc.halo.domain.notification.entity.Notification;
import com.umc.halo.domain.notification.enums.NotificationType;
import com.umc.halo.domain.notification.repository.AnniversaryRepository;
import com.umc.halo.domain.notification.repository.NotificationRepository;
import com.umc.halo.domain.setting.entity.MemberSetting;
import com.umc.halo.domain.setting.exception.SettingException;
import com.umc.halo.domain.setting.exception.code.SettingErrorCode;
import com.umc.halo.domain.setting.repository.MemberSettingRepository;
import com.umc.halo.global.ai.event.AnniversaryCreatedEvent;
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

        Anniversary anniversary = anniversaryRepository.findById(event.anniversaryId()).orElseThrow();
        MemberSetting memberSetting = memberSettingRepository.findByMemberId(anniversary.getMember().getId())
                .orElseThrow(() -> new SettingException(SettingErrorCode.SETTING_NOT_FOUND));

        try {
            String message = aiService.generateAnniversaryNotificationMessage(anniversary.getTitle(), anniversary.getMemo());

            LocalTime notifyTime = memberSetting.getRegularNotificationTime();
            LocalDateTime d7 = anniversary.getAnniversaryDate().minusDays(7).atTime(notifyTime);
            LocalDateTime dday = anniversary.getAnniversaryDate().atTime(notifyTime);

            Notification d7Notification = NotificationConverter.toAnniversaryNotification(anniversary, NotificationType.ANNIVERSARY_D7, message, d7);
            Notification ddayNotification = NotificationConverter.toAnniversaryNotification(anniversary, NotificationType.ANNIVERSARY_DDAY, message, dday);

            notificationRepository.saveAll(List.of(d7Notification, ddayNotification));

        } catch (AiException e) {
            log.warn("기념일 알림 문구 생성 실패. anniversaryId={}", event.anniversaryId(), e);
        }
    }
}
