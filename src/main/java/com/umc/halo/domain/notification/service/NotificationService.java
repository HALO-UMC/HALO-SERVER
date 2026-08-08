package com.umc.halo.domain.notification.service;

import com.umc.halo.domain.content.chapter.entity.Chapter;
import com.umc.halo.domain.content.chapter.repository.ChapterRepository;
import com.umc.halo.domain.content.storybook.dto.StorybookResDTO;
import com.umc.halo.domain.content.storybook.entity.Storybook;
import com.umc.halo.domain.content.storybook.service.StorybookService;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.entity.MemberDevice;
import com.umc.halo.domain.member.repository.MemberDeviceRepository;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.notification.converter.NotificationConverter;
import com.umc.halo.domain.notification.entity.Anniversary;
import com.umc.halo.domain.notification.entity.CommonAnniversary;
import com.umc.halo.domain.notification.entity.Notification;
import com.umc.halo.domain.notification.enums.NotificationType;
import com.umc.halo.domain.notification.repository.CommonAnniversaryRepository;
import com.umc.halo.domain.notification.repository.NotificationRepository;
import com.umc.halo.domain.record.entity.MemberChapter;
import com.umc.halo.domain.record.entity.MemberStorybook;
import com.umc.halo.domain.record.enums.Status;
import com.umc.halo.domain.record.repository.MemberChapterRepository;
import com.umc.halo.domain.record.repository.MemberStorybookRepository;
import com.umc.halo.domain.setting.entity.MemberSetting;
import com.umc.halo.domain.setting.exception.SettingException;
import com.umc.halo.domain.setting.exception.code.SettingErrorCode;
import com.umc.halo.domain.setting.repository.MemberSettingRepository;
import com.umc.halo.global.ai.event.NextAnniversaryNotificationCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationTransactionService notificationTransactionService;
    private final MemberDeviceRepository memberDeviceRepository;
    private final MemberSettingRepository memberSettingRepository;
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final MemberChapterRepository memberChapterRepository;
    private final MemberStorybookRepository memberStorybookRepository;
    private final ChapterRepository chapterRepository;
    private final CommonAnniversaryRepository commonAnniversaryRepository;
    private final FcmService fcmService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final StorybookService storybookService;

    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");

    public void sendScheduledNotifications() {

        List<Notification> notifications = notificationTransactionService.claimNotifications();

        for (Notification notification : notifications) {

            UUID leaseId = notification.getProcessingLeaseId();

            if (leaseId == null) {
                log.warn("processing lease가 없는 notificationId={}", notification.getId());
                continue;
            }

            try {

                if (!canSend(notification)) {
                    notificationTransactionService.expireSend(notification.getId(), leaseId);
                    continue;
                }

                boolean success = send(notification);

                if (success) {
                    notificationTransactionService.completeSend(notification.getId(), leaseId);
                } else {
                    notificationTransactionService.failSend(notification.getId(), leaseId);
                }

            } catch (Exception e) {
                notificationTransactionService.failSend(notification.getId(), leaseId);
            }
        }
    }

    @Transactional
    public void createNextYearNotifications() {

        List<Anniversary> anniversaries = notificationRepository.findAllRepeatedAnniversaries();

        for (Anniversary anniversary : anniversaries) {
            applicationEventPublisher.publishEvent(new NextAnniversaryNotificationCreatedEvent(anniversary.getId()));
        }
    }

    @Transactional
    public void createTodayChapterNotifications() {

        List<MemberSetting> memberSettings = memberSettingRepository.findAllWithMember();

        List<Member> members = memberSettings.stream().map(MemberSetting::getMember).toList();

        List<MemberStorybook> allMemberStorybooks = memberStorybookRepository.findAllByMemberIn(members);

        Map<Long, List<MemberStorybook>> memberStorybookMap = allMemberStorybooks.stream()
                .collect(Collectors.groupingBy(
                        ms -> ms.getMember().getId()
                ));

        List<MemberChapter> allMemberChapters = memberChapterRepository.findAllByMemberIn(members);

        Map<Long, List<MemberChapter>> memberChapterMap = allMemberChapters.stream()
                .collect(Collectors.groupingBy(
                        mc -> mc.getMember().getId()
                ));

        List<Storybook> storybooks = allMemberStorybooks.stream().map(MemberStorybook::getStorybook).distinct().toList();

        List<Chapter> chapters = chapterRepository.findByStorybookIn(storybooks);

        Map<String, Chapter> chapterMap = chapters.stream()
                .collect(Collectors.toMap(
                        c -> c.getStorybook().getId() + "_" + c.getChapterOrder(),
                        c -> c
                ));

        LocalDateTime scheduledAt = LocalDate.now(ZONE_ID).plusDays(1).atStartOfDay();

        for (MemberSetting memberSetting : memberSettings) {

            Member member = memberSetting.getMember();

            List<MemberStorybook> memberStorybooks = memberStorybookMap.getOrDefault(member.getId(), List.of());

            if (memberStorybooks.isEmpty()) {
                createRecommendNotification(member, memberSetting, scheduledAt);
                continue;
            }

            List<MemberChapter> memberChapters = memberChapterMap.getOrDefault(member.getId(), List.of());

            Map<Long, List<MemberChapter>> storybookChapterMap = memberChapters.stream()
                    .collect(Collectors.groupingBy(
                            mc -> mc.getChapter().getStorybook().getId()
                    ));

            Map<Long, MemberChapter> chapterStatusMap =
                    memberChapters.stream()
                            .collect(Collectors.toMap(
                                    mc -> mc.getChapter().getId(),
                                    mc -> mc
                            ));

            List<Chapter> todayChapters = findTodayChapters(memberStorybooks, storybookChapterMap, chapterStatusMap, chapterMap);

            if (todayChapters.isEmpty()) {
                continue;
            }

            createTodayChapterNotification(member, todayChapters, memberSetting, scheduledAt);
        }
    }

    public void createRetentionNotifications() {

        List<Long> memberIds = memberRepository.findAllIds();

        for (Long memberId : memberIds) {
            notificationTransactionService.createRetentionNotification(memberId);
        }
    }

    @Transactional
    public void createCommonAnniversaryNotifications() {

        LocalDate today = LocalDate.now(ZONE_ID);

        List<CommonAnniversary> commonAnniversaries = commonAnniversaryRepository.findByMonthAndDay(today.getMonthValue(), today.getDayOfMonth());

        if (commonAnniversaries.isEmpty()) {
            return;
        }

        List<MemberSetting> memberSettings = memberSettingRepository.findAllWithMember();

        for (MemberSetting memberSetting : memberSettings) {
            notificationTransactionService.createCommonAnniversaryNotification(memberSetting, commonAnniversaries, today);
        }
    }

    private boolean canSend(Notification notification) {
        MemberSetting memberSetting = memberSettingRepository.findByMemberId(notification.getMember().getId()).orElseThrow(() -> new SettingException(SettingErrorCode.SETTING_NOT_FOUND));

        if (!Boolean.TRUE.equals(memberSetting.getIsAllNotificationEnabled())) {
            return false;
        }

        return switch (notification.getNotificationType()) {
            case ANNIVERSARY_D7, ANNIVERSARY_DDAY -> Boolean.TRUE.equals(notification.getAnniversaryEnabled()) && Boolean.TRUE.equals(notification.getSettingEnabled());
            case TODAY_CHAPTER, RETENTION, COMMON_ANNIVERSARY -> Boolean.TRUE.equals(notification.getSettingEnabled());
        };
    }

    private boolean send(Notification notification) {
        List<MemberDevice> memberDevices = memberDeviceRepository.findAllByMember(notification.getMember());

        int successCount = 0;

        for (MemberDevice memberDevice : memberDevices) {
            try {
                boolean success = fcmService.send(memberDevice, notification.getTitle(), notification.getMessage());
                if (success) {
                    successCount++;
                }
            } catch (Exception e) {
                log.error("FCM 전송 실패 memberId={}, deviceId={}, notificationId={}", notification.getMember().getId(), memberDevice.getId(), notification.getId(), e);
            }
        }

        return successCount > 0;
    }

    private List<Chapter> findTodayChapters(List<MemberStorybook> memberStorybooks, Map<Long, List<MemberChapter>> storybookChapterMap, Map<Long, MemberChapter> memberChapterMap, Map<String, Chapter> chapterMap) {

        List<Chapter> result = new ArrayList<>();

        for (MemberStorybook memberStorybook : memberStorybooks) {

            Storybook storybook = memberStorybook.getStorybook();

            List<MemberChapter> memberChapters = storybookChapterMap.getOrDefault(storybook.getId(), List.of());

            Integer todayChapterOrder = memberStorybook.resolveTodayChapterOrder(memberChapters);
            if (todayChapterOrder == null) {
                continue;
            }

            Chapter chapter = chapterMap.get(storybook.getId() + "_" + todayChapterOrder);
            if (chapter == null) {
                continue;
            }

            MemberChapter memberChapter = memberChapterMap.get(chapter.getId());

            if (memberChapter == null || memberChapter.getStatus() == Status.DRAFT) {
                result.add(chapter);
            }
        }

        return result;
    }

    private void createTodayChapterNotification(Member member, List<Chapter> chapters, MemberSetting memberSetting, LocalDateTime scheduledAt) {

        boolean exists = notificationRepository.existsByMemberAndNotificationTypeAndScheduledAt(member, NotificationType.TODAY_CHAPTER, scheduledAt);

        if (exists) {
            return;
        }

        Notification notification;
        Boolean settingEnabled = memberSetting.getTodayChapterNotificationEnabled();

        if (chapters.size() == 1) {
            Chapter chapter = chapters.get(0);
            notification = NotificationConverter.toTodayChapterNotification(member, "📖 [" + chapter.getStorybook().getTitle() + "] 오늘의 새로운 페이지가 열렸어요.", "오늘도 부모님과의 이야기를 이어가 볼까요?", scheduledAt, settingEnabled);
        } else {
            notification = NotificationConverter.toTodayChapterNotification(member, "📖 오늘 작성할 새로운 페이지가 " + chapters.size() + "장 열렸어요.", "오늘도 부모님과의 이야기를 이어가 볼까요?", scheduledAt, settingEnabled);
        }

        notificationRepository.save(notification);
    }

    private void createRecommendNotification(Member member, MemberSetting memberSetting, LocalDateTime scheduledAt) {

        boolean exists = notificationRepository.existsByMemberAndNotificationTypeAndScheduledAt(member, NotificationType.TODAY_CHAPTER, scheduledAt);

        if (exists) {
            return;
        }

        List<StorybookResDTO.RecommendedStorybook> storybooks = storybookService.getRecommendedStorybooks(member.getId()).storybooks();

        if (storybooks.isEmpty()) {
            return;
        }

        String title = "📚 새로운 이야기를 시작해 볼까요?";
        String message = "오늘은 [" + storybooks.get(0).title();
        if (storybooks.size() >= 2) {
            message += "] 또는 [" + storybooks.get(1).title();
        }
        message += "] 추천드려요.";

        Notification notification = NotificationConverter.toTodayChapterNotification(member, title, message, scheduledAt, memberSetting.getTodayChapterNotificationEnabled());

        notificationRepository.save(notification);
    }
}
