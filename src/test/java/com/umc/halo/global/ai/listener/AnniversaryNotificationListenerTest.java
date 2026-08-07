package com.umc.halo.global.ai.listener;

import com.umc.halo.domain.notification.entity.Anniversary;
import com.umc.halo.domain.notification.entity.Notification;
import com.umc.halo.domain.notification.enums.NotificationStatus;
import com.umc.halo.domain.notification.enums.NotificationType;
import com.umc.halo.domain.notification.repository.AnniversaryRepository;
import com.umc.halo.domain.notification.repository.NotificationRepository;
import com.umc.halo.domain.setting.entity.MemberSetting;
import com.umc.halo.domain.setting.repository.MemberSettingRepository;
import com.umc.halo.global.ai.service.AiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * generateNotificationMessage/updateNotificationMessage에서 @Transactional(REQUIRES_NEW) 대신
 * saveOrUpdateNotification/updateOrCancelNotification/cancelNotification에 추가한 save() 호출이 실제로 반영되는지 검증
 * private 메서드라 리플렉션으로 직접 호출
 */
@ExtendWith(MockitoExtension.class)
class AnniversaryNotificationListenerTest {

    @Mock
    private AnniversaryRepository anniversaryRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private MemberSettingRepository memberSettingRepository;
    @Mock
    private AiService aiService;

    @InjectMocks
    private AnniversaryNotificationListener listener;

    @Test
    void 기존_알림이_있으면_수정_후_명시적으로_저장한다() throws Exception {
        Notification existing = Notification.builder()
                .id(1L)
                .notificationType(NotificationType.ANNIVERSARY_D7)
                .status(NotificationStatus.SCHEDULED)
                .build();
        given(notificationRepository.findByAnniversaryIdAndNotificationTypeAndStatusIn(
                eq(1L), eq(NotificationType.ANNIVERSARY_D7), anyList()))
                .willReturn(Optional.of(existing));

        Anniversary anniversary = Anniversary.builder().id(1L).build();
        MemberSetting memberSetting = MemberSetting.builder().anniversaryNotificationEnabled(true).build();
        LocalDateTime scheduledAt = LocalDateTime.now().plusDays(7);

        invokeSaveOrUpdateNotification(anniversary, memberSetting, NotificationType.ANNIVERSARY_D7,
                "제목", "메시지", scheduledAt);

        assertThat(existing.getScheduledAt()).isEqualTo(scheduledAt);
        verify(notificationRepository).save(existing);
    }

    @Test
    void 기존_알림이_없으면_새로_생성해서_저장한다() throws Exception {
        given(notificationRepository.findByAnniversaryIdAndNotificationTypeAndStatusIn(
                eq(1L), eq(NotificationType.ANNIVERSARY_D7), anyList()))
                .willReturn(Optional.empty());

        Anniversary anniversary = Anniversary.builder().id(1L).sevenDaysAlarmEnabled(true).build();
        MemberSetting memberSetting = MemberSetting.builder().anniversaryNotificationEnabled(true).build();
        LocalDateTime scheduledAt = LocalDateTime.now().plusDays(7);

        invokeSaveOrUpdateNotification(anniversary, memberSetting, NotificationType.ANNIVERSARY_D7,
                "제목", "메시지", scheduledAt);

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void 만료_대상이면_expire_처리_후_저장한다() throws Exception {
        Notification existing = Notification.builder()
                .id(3L)
                .notificationType(NotificationType.ANNIVERSARY_D7)
                .status(NotificationStatus.SCHEDULED)
                .build();
        given(notificationRepository.findByAnniversaryIdAndNotificationTypeAndStatusIn(
                eq(1L), eq(NotificationType.ANNIVERSARY_D7), anyList()))
                .willReturn(Optional.of(existing));

        Anniversary anniversary = Anniversary.builder().id(1L).build();
        MemberSetting memberSetting = MemberSetting.builder().anniversaryNotificationEnabled(true).build();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime pastScheduledAt = now.minusMinutes(1);

        Method method = AnniversaryNotificationListener.class.getDeclaredMethod(
                "updateOrCancelNotification", Anniversary.class, MemberSetting.class, NotificationType.class,
                String.class, String.class, LocalDateTime.class, LocalDateTime.class, boolean.class);
        method.setAccessible(true);
        method.invoke(listener, anniversary, memberSetting, NotificationType.ANNIVERSARY_D7,
                "제목", null, pastScheduledAt, now, true);

        assertThat(existing.getStatus()).isEqualTo(NotificationStatus.EXPIRED);
        verify(notificationRepository).save(existing);
    }

    @Test
    void cancelNotification은_기존_알림을_비활성화하고_저장한다() throws Exception {
        Notification existing = Notification.builder()
                .id(2L)
                .notificationType(NotificationType.ANNIVERSARY_DDAY)
                .status(NotificationStatus.SCHEDULED)
                .anniversaryEnabled(true)
                .build();
        given(notificationRepository.findByAnniversaryIdAndNotificationTypeAndStatusIn(
                eq(1L), eq(NotificationType.ANNIVERSARY_DDAY), anyList()))
                .willReturn(Optional.of(existing));

        Anniversary anniversary = Anniversary.builder().id(1L).build();

        Method method = AnniversaryNotificationListener.class.getDeclaredMethod(
                "cancelNotification", Anniversary.class, NotificationType.class);
        method.setAccessible(true);
        method.invoke(listener, anniversary, NotificationType.ANNIVERSARY_DDAY);

        assertThat(existing.getAnniversaryEnabled()).isFalse();
        verify(notificationRepository).save(existing);
    }

    private void invokeSaveOrUpdateNotification(Anniversary anniversary, MemberSetting memberSetting,
                                                NotificationType notificationType, String title,
                                                String message, LocalDateTime scheduledAt) throws Exception {
        Method method = AnniversaryNotificationListener.class.getDeclaredMethod(
                "saveOrUpdateNotification", Anniversary.class, MemberSetting.class, NotificationType.class,
                String.class, String.class, LocalDateTime.class);
        method.setAccessible(true);
        method.invoke(listener, anniversary, memberSetting, notificationType, title, message, scheduledAt);
    }
}