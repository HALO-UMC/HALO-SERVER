package com.umc.halo.global.ai.listener;

import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.notification.entity.Anniversary;
import com.umc.halo.domain.notification.repository.AnniversaryRepository;
import com.umc.halo.domain.notification.service.NotificationTransactionService;
import com.umc.halo.domain.setting.entity.MemberSetting;
import com.umc.halo.domain.setting.repository.MemberSettingRepository;
import com.umc.halo.global.ai.event.AnniversaryCreatedEvent;
import com.umc.halo.global.ai.event.AnniversaryUpdatedEvent;
import com.umc.halo.global.ai.event.CreateNextYearNotificationEvent;
import com.umc.halo.global.ai.service.AiService;
import com.umc.halo.global.util.AnniversaryOccurrenceResolver;
import com.umc.halo.global.ai.exception.AiException;
import com.umc.halo.global.ai.exception.code.AiErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

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
    void generateNotificationMessage는_메모가_있으면_AI가_생성한_문구를_사용한다() {
        Anniversary anniversary = Anniversary.builder()
                .id(1L)
                .member(member)
                .title("결혼기념일")
                .memo("우리가 처음 만난 날")
                .anniversaryDate(LocalDate.now().plusDays(30))
                .isRepeated(false)
                .sevenDaysAlarmEnabled(true)
                .dayAlarmEnabled(true)
                .build();

        given(anniversaryRepository.findById(1L)).willReturn(Optional.of(anniversary));
        given(anniversaryRepository.findMemberIdById(1L)).willReturn(Optional.of(5L));
        given(memberSettingRepository.findByMemberId(any())).willReturn(Optional.of(memberSetting));
        given(aiService.generateAnniversaryNotificationMessage(5L, "결혼기념일", "우리가 처음 만난 날"))
                .willReturn("AI가 만든 문구");

        listener.generateNotificationMessage(new AnniversaryCreatedEvent(1L));

        ArgumentCaptor<String> d7MessageCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> ddayMessageCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificationTransactionService).saveOrUpdateBoth(
                eq(anniversary), eq(memberSetting),
                any(), d7MessageCaptor.capture(), any(),
                any(), ddayMessageCaptor.capture(), any(),
                any());

        assertThat(d7MessageCaptor.getValue()).isEqualTo("AI가 만든 문구");
        assertThat(ddayMessageCaptor.getValue()).isEqualTo("AI가 만든 문구");
    }

    @Test
    void generateNotificationMessage는_AI_호출이_실패하면_기본_문구로_폴백한다() {
        Anniversary anniversary = Anniversary.builder()
                .id(1L)
                .member(member)
                .title("결혼기념일")
                .memo("우리가 처음 만난 날")
                .anniversaryDate(LocalDate.now().plusDays(30))
                .isRepeated(false)
                .sevenDaysAlarmEnabled(true)
                .dayAlarmEnabled(true)
                .build();

        given(anniversaryRepository.findById(1L)).willReturn(Optional.of(anniversary));
        given(anniversaryRepository.findMemberIdById(1L)).willReturn(Optional.of(5L));
        given(memberSettingRepository.findByMemberId(any())).willReturn(Optional.of(memberSetting));
        given(aiService.generateAnniversaryNotificationMessage(5L, "결혼기념일", "우리가 처음 만난 날"))
                .willThrow(new AiException(AiErrorCode.AI_GENERATE_FAILED));

        listener.generateNotificationMessage(new AnniversaryCreatedEvent(1L));

        ArgumentCaptor<String> d7MessageCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> ddayMessageCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificationTransactionService).saveOrUpdateBoth(
                eq(anniversary), eq(memberSetting),
                any(), d7MessageCaptor.capture(), any(),
                any(), ddayMessageCaptor.capture(), any(),
                any());

        assertThat(d7MessageCaptor.getValue()).isEqualTo("오늘부터 조금씩 마음을 준비해 보세요.");
        assertThat(ddayMessageCaptor.getValue()).isEqualTo("오늘의 따뜻한 안녕을 전해보세요.");
        verify(aiService, times(2)).generateAnniversaryNotificationMessage(5L, "결혼기념일", "우리가 처음 만난 날");
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

    @Test
    void createNextNotification은_반복_기념일이_아니면_아무것도_하지_않는다() {
        Anniversary anniversary = Anniversary.builder()
                .id(1L)
                .member(member)
                .title("결혼기념일")
                .anniversaryDate(LocalDate.now().plusDays(30))
                .isRepeated(false)
                .build();

        given(anniversaryRepository.findById(1L)).willReturn(Optional.of(anniversary));

        listener.createNextNotification(new CreateNextYearNotificationEvent(1L));

        verify(anniversaryRepository, never()).findMemberIdById(any());
        verifyNoInteractions(memberSettingRepository, notificationTransactionService);
    }

    @Test
    void createNextNotification은_회원설정이_없으면_아무것도_하지_않는다() {
        Anniversary anniversary = Anniversary.builder()
                .id(1L)
                .member(member)
                .title("결혼기념일")
                .anniversaryDate(LocalDate.now().plusDays(30))
                .isRepeated(true)
                .build();

        given(anniversaryRepository.findById(1L)).willReturn(Optional.of(anniversary));
        given(anniversaryRepository.findMemberIdById(1L)).willReturn(Optional.of(5L));
        given(memberSettingRepository.findByMemberId(5L)).willReturn(Optional.empty());

        listener.createNextNotification(new CreateNextYearNotificationEvent(1L));

        verify(anniversaryRepository).findMemberIdById(1L);
        verify(memberSettingRepository).findByMemberId(5L);
        verifyNoInteractions(notificationTransactionService);
    }

    @Test
    void createNextNotification은_다음_발생일이_없으면_아무것도_하지_않는다() {
        Anniversary anniversary = Anniversary.builder()
                .id(1L)
                .member(member)
                .title("결혼기념일")
                .anniversaryDate(LocalDate.now().plusDays(30))
                .isRepeated(true)
                .build();

        given(anniversaryRepository.findById(1L)).willReturn(Optional.of(anniversary));
        given(anniversaryRepository.findMemberIdById(1L)).willReturn(Optional.of(5L));
        given(memberSettingRepository.findByMemberId(5L)).willReturn(Optional.of(memberSetting));

        try (MockedStatic<AnniversaryOccurrenceResolver> mockedResolver = mockStatic(AnniversaryOccurrenceResolver.class)) {
            mockedResolver.when(() -> AnniversaryOccurrenceResolver.resolveNextOccurrence(eq(anniversary), any(LocalDate.class)))
                    .thenReturn(null);

            listener.createNextNotification(new CreateNextYearNotificationEvent(1L));

            mockedResolver.verify(() -> AnniversaryOccurrenceResolver.resolveNextOccurrence(eq(anniversary), any(LocalDate.class)));
        }

        verifyNoInteractions(notificationTransactionService);
    }

    @Test
    void createNextNotification은_알림이_비활성화되어_있으면_메시지_없이_저장한다() {
        Anniversary anniversary = Anniversary.builder()
                .id(1L)
                .member(member)
                .title("결혼기념일")
                .anniversaryDate(LocalDate.now().plusDays(30))
                .isRepeated(true)
                .sevenDaysAlarmEnabled(false)
                .dayAlarmEnabled(false)
                .build();

        given(anniversaryRepository.findById(1L)).willReturn(Optional.of(anniversary));
        given(anniversaryRepository.findMemberIdById(1L)).willReturn(Optional.of(5L));
        given(memberSettingRepository.findByMemberId(5L)).willReturn(Optional.of(memberSetting));

        listener.createNextNotification(new CreateNextYearNotificationEvent(1L));

        ArgumentCaptor<String> d7MessageCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> ddayMessageCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificationTransactionService).saveOrUpdateBoth(
                eq(anniversary), eq(memberSetting),
                any(), d7MessageCaptor.capture(), any(),
                any(), ddayMessageCaptor.capture(), any(),
                any());

        assertThat(d7MessageCaptor.getValue()).isNull();
        assertThat(ddayMessageCaptor.getValue()).isNull();
    }

    @Test
    void createNextNotification은_D7만_활성화되어_있으면_D7_메시지만_채운다() {
        Anniversary anniversary = Anniversary.builder()
                .id(1L)
                .member(member)
                .title("결혼기념일")
                .anniversaryDate(LocalDate.now().plusDays(30))
                .isRepeated(true)
                .sevenDaysAlarmEnabled(true)
                .dayAlarmEnabled(false)
                .build();

        given(anniversaryRepository.findById(1L)).willReturn(Optional.of(anniversary));
        given(anniversaryRepository.findMemberIdById(1L)).willReturn(Optional.of(5L));
        given(memberSettingRepository.findByMemberId(5L)).willReturn(Optional.of(memberSetting));

        listener.createNextNotification(new CreateNextYearNotificationEvent(1L));

        ArgumentCaptor<String> d7MessageCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> ddayMessageCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificationTransactionService).saveOrUpdateBoth(
                eq(anniversary), eq(memberSetting),
                any(), d7MessageCaptor.capture(), any(),
                any(), ddayMessageCaptor.capture(), any(),
                any());

        assertThat(d7MessageCaptor.getValue()).isEqualTo("오늘부터 조금씩 마음을 준비해 보세요.");
        assertThat(ddayMessageCaptor.getValue()).isNull();
    }

    @Test
    void createNextNotification은_DDay만_활성화되어_있으면_DDay_메시지만_채운다() {
        Anniversary anniversary = Anniversary.builder()
                .id(1L)
                .member(member)
                .title("결혼기념일")
                .anniversaryDate(LocalDate.now().plusDays(30))
                .isRepeated(true)
                .sevenDaysAlarmEnabled(false)
                .dayAlarmEnabled(true)
                .build();

        given(anniversaryRepository.findById(1L)).willReturn(Optional.of(anniversary));
        given(anniversaryRepository.findMemberIdById(1L)).willReturn(Optional.of(5L));
        given(memberSettingRepository.findByMemberId(5L)).willReturn(Optional.of(memberSetting));

        listener.createNextNotification(new CreateNextYearNotificationEvent(1L));

        ArgumentCaptor<String> d7MessageCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> ddayMessageCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificationTransactionService).saveOrUpdateBoth(
                eq(anniversary), eq(memberSetting),
                any(), d7MessageCaptor.capture(), any(),
                any(), ddayMessageCaptor.capture(), any(),
                any());

        assertThat(d7MessageCaptor.getValue()).isNull();
        assertThat(ddayMessageCaptor.getValue()).isEqualTo("오늘의 따뜻한 안녕을 전해보세요.");
    }

    @Test
    void createNextNotification은_D7_발생일이_이미_지났으면_DDay_메시지만_채운다() {
        Anniversary anniversary = Anniversary.builder()
                .id(1L)
                .member(member)
                .title("결혼기념일")
                .anniversaryDate(LocalDate.now().plusDays(3))
                .isRepeated(true)
                .sevenDaysAlarmEnabled(true)
                .dayAlarmEnabled(true)
                .build();

        given(anniversaryRepository.findById(1L)).willReturn(Optional.of(anniversary));
        given(anniversaryRepository.findMemberIdById(1L)).willReturn(Optional.of(5L));
        given(memberSettingRepository.findByMemberId(5L)).willReturn(Optional.of(memberSetting));

        listener.createNextNotification(new CreateNextYearNotificationEvent(1L));

        ArgumentCaptor<String> d7MessageCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> ddayMessageCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificationTransactionService).saveOrUpdateBoth(
                eq(anniversary), eq(memberSetting),
                any(), d7MessageCaptor.capture(), any(),
                any(), ddayMessageCaptor.capture(), any(),
                any());

        assertThat(d7MessageCaptor.getValue()).isNull();
        assertThat(ddayMessageCaptor.getValue()).isEqualTo("오늘의 따뜻한 안녕을 전해보세요.");
    }

    @Test
    void createNextNotification은_D7_DDay_메시지를_묶어서_한번에_넘긴다() {
        Anniversary anniversary = Anniversary.builder()
                .id(1L)
                .member(member)
                .title("결혼기념일")
                .anniversaryDate(LocalDate.now().plusDays(30))
                .isRepeated(true)
                .sevenDaysAlarmEnabled(true)
                .dayAlarmEnabled(true)
                .build();

        given(anniversaryRepository.findById(1L)).willReturn(Optional.of(anniversary));
        given(anniversaryRepository.findMemberIdById(1L)).willReturn(Optional.of(5L));
        given(memberSettingRepository.findByMemberId(5L)).willReturn(Optional.of(memberSetting));

        listener.createNextNotification(new CreateNextYearNotificationEvent(1L));

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
}