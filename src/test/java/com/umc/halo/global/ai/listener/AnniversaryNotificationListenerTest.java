package com.umc.halo.global.ai.listener;

import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.notification.entity.Anniversary;
import com.umc.halo.domain.notification.repository.AnniversaryRepository;
import com.umc.halo.domain.notification.service.NotificationTransactionService;
import com.umc.halo.domain.setting.entity.MemberSetting;
import com.umc.halo.domain.setting.repository.MemberSettingRepository;
import com.umc.halo.global.ai.event.AnniversaryCreatedEvent;
import com.umc.halo.global.ai.event.AnniversaryUpdatedEvent;
import com.umc.halo.global.ai.service.AiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * generateNotificationMessage/updateNotificationMessage는 AI 문구 생성
 * 실제 DB 반영은 NotificationTransactionService에 D7/DDay를 한 번에(원자적으로) 위임하는지를 검증
 */
@ExtendWith(MockitoExtension.class)
class AnniversaryNotificationListenerTest {

    @Mock
    private AnniversaryRepository anniversaryRepository;
    @Mock
    private MemberSettingRepository memberSettingRepository;
    @Mock
    private AiService aiService;
    @Mock
    private NotificationTransactionService notificationTransactionService;

    @InjectMocks
    private AnniversaryNotificationListener listener;

    private final MemberSetting memberSetting = MemberSetting.builder()
            .regularNotificationTime(LocalTime.of(9, 0))
            .build();

    private final Member member = Member.builder().id(5L).build();

    @Test
    void generateNotificationMessage는_D7_DDay_둘다_묶어서_한번에_넘긴다() {
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
        given(anniversaryRepository.findMemberIdById(1L)).willReturn(Optional.of(5L));
        given(memberSettingRepository.findByMemberId(any())).willReturn(Optional.of(memberSetting));

        listener.generateNotificationMessage(new AnniversaryCreatedEvent(1L));

        ArgumentCaptor<String> d7TitleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> d7MessageCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> ddayTitleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> ddayMessageCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificationTransactionService).saveOrUpdateBoth(
                eq(anniversary), eq(memberSetting),
                d7TitleCaptor.capture(), d7MessageCaptor.capture(), any(),
                ddayTitleCaptor.capture(), ddayMessageCaptor.capture(), any(),
                any());

        assertThat(d7TitleCaptor.getValue()).isEqualTo("결혼기념일까지 7일 남았어요.");
        assertThat(ddayTitleCaptor.getValue()).isEqualTo("오늘은 결혼기념일입니다.");
        assertThat(d7MessageCaptor.getValue()).isEqualTo("오늘부터 조금씩 마음을 준비해 보세요.");
        assertThat(ddayMessageCaptor.getValue()).isEqualTo("오늘의 따뜻한 안녕을 전해보세요.");
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
        given(anniversaryRepository.findMemberIdById(1L)).willReturn(Optional.of(5L));
        given(memberSettingRepository.findByMemberId(any())).willReturn(Optional.of(memberSetting));

        listener.updateNotificationMessage(new AnniversaryUpdatedEvent(1L, false, false));

        verify(notificationTransactionService).cancelBoth(anniversary);
        verify(notificationTransactionService, never()).updateOrCancelBoth(
                any(), any(), any(), any(), any(), anyBoolean(), any(), any(), any(), anyBoolean(), any());
    }


    @Test
    void updateNotificationMessage는_D7_DDay_내용을_묶어서_한번에_넘긴다() {
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
        given(anniversaryRepository.findMemberIdById(1L)).willReturn(Optional.of(5L));
        given(memberSettingRepository.findByMemberId(any())).willReturn(Optional.of(memberSetting));

        listener.updateNotificationMessage(new AnniversaryUpdatedEvent(1L, true, false));

        ArgumentCaptor<String> d7MessageCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Boolean> d7EnabledCaptor = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<String> ddayMessageCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Boolean> ddayEnabledCaptor = ArgumentCaptor.forClass(Boolean.class);
        verify(notificationTransactionService).updateOrCancelBoth(
                eq(anniversary), eq(memberSetting),
                any(), d7MessageCaptor.capture(), any(), d7EnabledCaptor.capture(),
                any(), ddayMessageCaptor.capture(), any(), ddayEnabledCaptor.capture(),
                any());

        assertThat(d7EnabledCaptor.getValue()).isTrue();
        assertThat(ddayEnabledCaptor.getValue()).isTrue();
        assertThat(d7MessageCaptor.getValue()).isEqualTo("오늘부터 조금씩 마음을 준비해 보세요.");
        assertThat(ddayMessageCaptor.getValue()).isEqualTo("오늘의 따뜻한 안녕을 전해보세요.");
        verify(notificationTransactionService, never()).cancelBoth(any());
    }
}