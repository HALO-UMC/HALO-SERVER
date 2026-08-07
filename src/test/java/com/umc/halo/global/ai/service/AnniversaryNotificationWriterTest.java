package com.umc.halo.global.ai.service;

import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.notification.entity.Anniversary;
import com.umc.halo.domain.notification.entity.Notification;
import com.umc.halo.domain.notification.enums.NotificationStatus;
import com.umc.halo.domain.notification.enums.NotificationType;
import com.umc.halo.domain.notification.repository.AnniversaryRepository;
import com.umc.halo.domain.notification.repository.NotificationRepository;
import com.umc.halo.domain.setting.entity.MemberSetting;
import com.umc.halo.domain.setting.repository.MemberSettingRepository;
import com.umc.halo.global.ai.service.AnniversaryNotificationWriter.NotificationContent;
import com.umc.halo.global.ai.service.AnniversaryNotificationWriter.UpdateContent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * D7/DDay 알림 저장/갱신/취소가 각각 하나의 @Transactional 메서드 안에서 함께 처리되는지 검증
 */
@ExtendWith(MockitoExtension.class)
class AnniversaryNotificationWriterTest {

    @Mock
    private AnniversaryRepository anniversaryRepository;
    @Mock
    private MemberSettingRepository memberSettingRepository;
    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private AnniversaryNotificationWriter writer;

    private final Member member = Member.builder().id(5L).build();
    private final Anniversary anniversary = Anniversary.builder().id(1L).member(member).build();
    private final MemberSetting memberSetting = MemberSetting.builder().anniversaryNotificationEnabled(true).build();

    @Test
    void saveGenerated는_D7과_DDay_둘다_전달되면_둘다_저장한다() {
        given(anniversaryRepository.findById(1L)).willReturn(Optional.of(anniversary));
        given(memberSettingRepository.findByMemberId(5L)).willReturn(Optional.of(memberSetting));
        given(notificationRepository.findByAnniversaryIdAndNotificationTypeAndStatusIn(eq(1L), any(), anyList()))
                .willReturn(Optional.empty());

        NotificationContent d7 = new NotificationContent("D7제목", "D7메시지", LocalDateTime.now().plusDays(1));
        NotificationContent dday = new NotificationContent("DDay제목", "DDay메시지", LocalDateTime.now().plusDays(8));

        writer.saveGenerated(1L, d7, dday);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(2)).save(captor.capture());
        assertThat(captor.getAllValues()).extracting(Notification::getNotificationType)
                .containsExactlyInAnyOrder(NotificationType.ANNIVERSARY_D7, NotificationType.ANNIVERSARY_DDAY);
    }

    @Test
    void saveGenerated는_dday가_null이면_D7만_저장한다() {
        given(anniversaryRepository.findById(1L)).willReturn(Optional.of(anniversary));
        given(memberSettingRepository.findByMemberId(5L)).willReturn(Optional.of(memberSetting));
        given(notificationRepository.findByAnniversaryIdAndNotificationTypeAndStatusIn(eq(1L), eq(NotificationType.ANNIVERSARY_D7), anyList()))
                .willReturn(Optional.empty());

        NotificationContent d7 = new NotificationContent("D7제목", "D7메시지", LocalDateTime.now().plusDays(1));

        writer.saveGenerated(1L, d7, null);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void updateGenerated는_기존_알림_둘다_새_일정으로_갱신하고_저장한다() {
        Notification existingD7 = Notification.builder().id(10L).notificationType(NotificationType.ANNIVERSARY_D7).status(NotificationStatus.SCHEDULED).build();
        Notification existingDday = Notification.builder().id(11L).notificationType(NotificationType.ANNIVERSARY_DDAY).status(NotificationStatus.SCHEDULED).build();

        given(anniversaryRepository.findById(1L)).willReturn(Optional.of(anniversary));
        given(memberSettingRepository.findByMemberId(5L)).willReturn(Optional.of(memberSetting));
        given(notificationRepository.findByAnniversaryIdAndNotificationTypeAndStatusIn(eq(1L), eq(NotificationType.ANNIVERSARY_D7), anyList()))
                .willReturn(Optional.of(existingD7));
        given(notificationRepository.findByAnniversaryIdAndNotificationTypeAndStatusIn(eq(1L), eq(NotificationType.ANNIVERSARY_DDAY), anyList()))
                .willReturn(Optional.of(existingDday));

        LocalDateTime now = LocalDateTime.now();
        UpdateContent d7 = new UpdateContent("D7제목", "D7메시지", now.plusDays(1), true);
        UpdateContent dday = new UpdateContent("DDay제목", "DDay메시지", now.plusDays(8), true);

        writer.updateGenerated(1L, now, d7, dday);

        assertThat(existingD7.getScheduledAt()).isEqualTo(now.plusDays(1));
        assertThat(existingDday.getScheduledAt()).isEqualTo(now.plusDays(8));
        verify(notificationRepository).save(existingD7);
        verify(notificationRepository).save(existingDday);
    }

    @Test
    void cancelBoth은_D7과_DDay_둘다_비활성화하고_저장한다() {
        Notification existingD7 = Notification.builder().id(10L).notificationType(NotificationType.ANNIVERSARY_D7).status(NotificationStatus.SCHEDULED).anniversaryEnabled(true).build();
        Notification existingDday = Notification.builder().id(11L).notificationType(NotificationType.ANNIVERSARY_DDAY).status(NotificationStatus.SCHEDULED).anniversaryEnabled(true).build();

        given(anniversaryRepository.findById(1L)).willReturn(Optional.of(anniversary));
        given(notificationRepository.findByAnniversaryIdAndNotificationTypeAndStatusIn(eq(1L), eq(NotificationType.ANNIVERSARY_D7), anyList()))
                .willReturn(Optional.of(existingD7));
        given(notificationRepository.findByAnniversaryIdAndNotificationTypeAndStatusIn(eq(1L), eq(NotificationType.ANNIVERSARY_DDAY), anyList()))
                .willReturn(Optional.of(existingDday));

        writer.cancelBoth(1L);

        assertThat(existingD7.getAnniversaryEnabled()).isFalse();
        assertThat(existingDday.getAnniversaryEnabled()).isFalse();
        verify(notificationRepository).save(existingD7);
        verify(notificationRepository).save(existingDday);
    }
}