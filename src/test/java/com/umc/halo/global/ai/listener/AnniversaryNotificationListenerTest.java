package com.umc.halo.global.ai.listener;

import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.notification.entity.Anniversary;
import com.umc.halo.domain.notification.repository.AnniversaryRepository;
import com.umc.halo.domain.notification.repository.NotificationRepository;
import com.umc.halo.domain.setting.entity.MemberSetting;
import com.umc.halo.domain.setting.repository.MemberSettingRepository;
import com.umc.halo.global.ai.event.AnniversaryCreatedEvent;
import com.umc.halo.global.ai.event.AnniversaryUpdatedEvent;
import com.umc.halo.global.ai.service.AiService;
import com.umc.halo.global.ai.service.AnniversaryNotificationWriter;
import com.umc.halo.global.ai.service.AnniversaryNotificationWriter.NotificationContent;
import com.umc.halo.global.ai.service.AnniversaryNotificationWriter.UpdateContent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * generateNotificationMessage/updateNotificationMessage는 AI 문구 생성
 * 실제 DB 반영은 AnniversaryNotificationWriter에 D7/DDay를 한 번에 위임하는지를 검증
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
    @Mock
    private AnniversaryNotificationWriter anniversaryNotificationWriter;

    @InjectMocks
    private AnniversaryNotificationListener listener;

    private final MemberSetting memberSetting = MemberSetting.builder()
            .regularNotificationTime(LocalTime.of(9, 0))
            .build();

    private final Member member = Member.builder().id(5L).build();

    @Test
    void generateNotificationMessage는_D7_DDay_둘다_활성화면_writer에_한번에_넘긴다() {
        Anniversary anniversary = Anniversary.builder()
                .id(1L)
                .member(member)
                .title("결혼기념일")
                .anniversaryDate(LocalDate.now().plusDays(30))
                .isRepeated(false)
                .sevenDaysAlarmEnabled(true)
                .dayAlarmEnabled(true)
                .build();

        given(anniversaryRepository.findById(1L)).willReturn(Optional.of(anniversary));
        given(memberSettingRepository.findByMemberId(any())).willReturn(Optional.of(memberSetting));

        listener.generateNotificationMessage(new AnniversaryCreatedEvent(1L));

        ArgumentCaptor<NotificationContent> d7Captor = ArgumentCaptor.forClass(NotificationContent.class);
        ArgumentCaptor<NotificationContent> ddayCaptor = ArgumentCaptor.forClass(NotificationContent.class);
        verify(anniversaryNotificationWriter).saveGenerated(eq(1L), d7Captor.capture(), ddayCaptor.capture());

        assertThat(d7Captor.getValue()).isNotNull();
        assertThat(ddayCaptor.getValue()).isNotNull();
        assertThat(d7Captor.getValue().title()).contains("7일");
    }

    @Test
    void dayAlarmEnabled가_false면_dday_content는_null로_전달된다() {
        Anniversary anniversary = Anniversary.builder()
                .id(1L)
                .member(member)
                .title("결혼기념일")
                .anniversaryDate(LocalDate.now().plusDays(30))
                .isRepeated(false)
                .sevenDaysAlarmEnabled(true)
                .dayAlarmEnabled(false)
                .build();

        given(anniversaryRepository.findById(1L)).willReturn(Optional.of(anniversary));
        given(memberSettingRepository.findByMemberId(any())).willReturn(Optional.of(memberSetting));

        listener.generateNotificationMessage(new AnniversaryCreatedEvent(1L));

        ArgumentCaptor<NotificationContent> ddayCaptor = ArgumentCaptor.forClass(NotificationContent.class);
        verify(anniversaryNotificationWriter).saveGenerated(eq(1L), any(), ddayCaptor.capture());

        assertThat(ddayCaptor.getValue()).isNull();
    }

    @Test
    void updateNotificationMessage에서_다음_발생일이_없으면_cancelBoth만_호출한다() {
        Anniversary anniversary = Anniversary.builder()
                .id(1L)
                .member(member)
                .title("결혼기념일")
                .anniversaryDate(LocalDate.now().minusDays(1))
                .isRepeated(false)
                .build();

        given(anniversaryRepository.findById(1L)).willReturn(Optional.of(anniversary));
        given(memberSettingRepository.findByMemberId(any())).willReturn(Optional.of(memberSetting));

        listener.updateNotificationMessage(new AnniversaryUpdatedEvent(1L, false, false));

        verify(anniversaryNotificationWriter).cancelBoth(1L);
        verify(anniversaryNotificationWriter, never()).updateGenerated(any(), any(), any(), any());
    }

    @Test
    void updateNotificationMessage는_D7_DDay_내용을_묶어서_writer에_넘긴다() {
        Anniversary anniversary = Anniversary.builder()
                .id(1L)
                .member(member)
                .title("결혼기념일")
                .anniversaryDate(LocalDate.now().plusDays(30))
                .isRepeated(false)
                .sevenDaysAlarmEnabled(true)
                .dayAlarmEnabled(true)
                .build();

        given(anniversaryRepository.findById(1L)).willReturn(Optional.of(anniversary));
        given(memberSettingRepository.findByMemberId(any())).willReturn(Optional.of(memberSetting));

        listener.updateNotificationMessage(new AnniversaryUpdatedEvent(1L, true, false));

        ArgumentCaptor<UpdateContent> d7Captor = ArgumentCaptor.forClass(UpdateContent.class);
        ArgumentCaptor<UpdateContent> ddayCaptor = ArgumentCaptor.forClass(UpdateContent.class);
        verify(anniversaryNotificationWriter).updateGenerated(
                eq(1L), any(), d7Captor.capture(), ddayCaptor.capture());

        assertThat(d7Captor.getValue().enabled()).isTrue();
        assertThat(ddayCaptor.getValue().enabled()).isTrue();
        verify(anniversaryNotificationWriter, never()).cancelBoth(any());
    }
}