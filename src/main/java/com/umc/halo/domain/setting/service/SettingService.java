package com.umc.halo.domain.setting.service;

import com.umc.halo.domain.notification.entity.Anniversary;
import com.umc.halo.domain.notification.entity.Notification;
import com.umc.halo.domain.notification.enums.NotificationStatus;
import com.umc.halo.domain.notification.enums.NotificationType;
import com.umc.halo.domain.notification.repository.NotificationRepository;
import com.umc.halo.domain.setting.converter.SettingConverter;
import com.umc.halo.domain.setting.dto.SettingReqDTO;
import com.umc.halo.domain.setting.dto.SettingResDTO;
import com.umc.halo.domain.setting.entity.Bgm;
import com.umc.halo.domain.setting.entity.MemberSetting;
import com.umc.halo.domain.setting.exception.SettingException;
import com.umc.halo.domain.setting.exception.code.SettingErrorCode;
import com.umc.halo.domain.setting.repository.BgmRepository;
import com.umc.halo.domain.setting.repository.MemberSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final MemberSettingRepository memberSettingRepository;
    private final BgmRepository bgmRepository;
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public SettingResDTO.NotificationSettings getNotificationSettings(Long memberId) {
        MemberSetting memberSetting = memberSettingRepository.findByMemberId(memberId)
                .orElseThrow(() -> new SettingException(SettingErrorCode.SETTING_NOT_FOUND));

        return SettingConverter.toNotificationSettings(memberSetting);
    }

    @Transactional(readOnly = true)
    public SettingResDTO.BgmSettings getBgmSettings(Long memberId) {
        MemberSetting memberSetting = memberSettingRepository.findByMemberId(memberId)
                .orElseThrow(() -> new SettingException(SettingErrorCode.SETTING_NOT_FOUND));

        return SettingConverter.toBgmSettings(memberSetting);
    }

    @Transactional(readOnly = true)
    public SettingResDTO.Bgms getBgms() {
        List<SettingResDTO.BgmInfo> bgms = bgmRepository.findAll().stream().map(SettingConverter::toBgmInfo).toList();
        return SettingConverter.toBgms(bgms);
    }

    @Transactional
    public SettingResDTO.NotificationSettings updateNotificationSettings(Long memberId, SettingReqDTO.UpdateNotificationSettings dto) {
        MemberSetting memberSetting = memberSettingRepository.findByMemberId(memberId)
                .orElseThrow(() -> new SettingException(SettingErrorCode.SETTING_NOT_FOUND));

        LocalTime previousNotificationTime = memberSetting.getRegularNotificationTime();
        boolean previousAnniversaryEnabled = memberSetting.getAnniversaryNotificationEnabled();

        memberSetting.updateNotificationSettings(
                dto.regularNotificationTime(),
                dto.todayChapterNotificationEnabled(),
                dto.retentionNotificationEnabled(),
                dto.anniversaryNotificationEnabled()
        );

        if (!Objects.equals(previousNotificationTime, dto.regularNotificationTime())) {
            updateNotificationScheduledTime(memberId, dto.regularNotificationTime());
        }

        if (previousAnniversaryEnabled != dto.anniversaryNotificationEnabled()) {
            updateAnniversaryNotifications(memberId, dto.anniversaryNotificationEnabled(), memberSetting);
        }

        return SettingConverter.toNotificationSettings(memberSetting);
    }

    @Transactional
    public SettingResDTO.BgmSettings updateBgmSettings(Long memberId, SettingReqDTO.UpdateBgmSettings dto) {
        MemberSetting memberSetting = memberSettingRepository.findByMemberId(memberId)
                .orElseThrow(() -> new SettingException(SettingErrorCode.SETTING_NOT_FOUND));

        Bgm bgm = null;

        if (dto.bgmEnabled() == null) {
            throw new SettingException(SettingErrorCode.BGM_ENABLED_REQUIRED);
        }

        if (dto.bgmVolume() == null) {
            throw new SettingException(SettingErrorCode.BGM_VOLUME_REQUIRED);
        }

        if (dto.bgmVolume() < 0 || dto.bgmVolume() > 100) {
            throw new SettingException(SettingErrorCode.INVALID_BGM_VOLUME);
        }

        if(dto.bgmId() != null) {
            bgm = bgmRepository.findById(dto.bgmId())
                    .orElseThrow(() -> new SettingException(SettingErrorCode.BGM_NOT_FOUND));
        }

        memberSetting.updateBgmSettings(bgm, dto.bgmEnabled(), dto.bgmVolume());

        return SettingConverter.toBgmSettings(memberSetting);
    }

    private void updateNotificationScheduledTime(Long memberId, LocalTime notifyTime) {

        List<Notification> notifications = notificationRepository.findAllWithAnniversary(memberId, List.of(NotificationType.ANNIVERSARY_D7, NotificationType.ANNIVERSARY_DDAY), List.of(NotificationStatus.SCHEDULED, NotificationStatus.EXPIRED));
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        for(Notification notification : notifications) {

            Anniversary anniversary = notification.getAnniversary();

            if (anniversary == null) {
                continue;
            }

            LocalDate nextOccurrence = resolveNextOccurrence(anniversary, today);

            if(nextOccurrence == null) {
                continue;
            }

            LocalDateTime scheduledAt;

            if(notification.getNotificationType() == NotificationType.ANNIVERSARY_D7) {
                scheduledAt = nextOccurrence.minusDays(7).atTime(notifyTime);
            } else if(notification.getNotificationType() == NotificationType.ANNIVERSARY_DDAY) {
                scheduledAt = nextOccurrence.atTime(notifyTime);
            } else {
                continue;
            }

            if (!scheduledAt.isAfter(now)) {
                notification.expire();
                continue;
            }

            if (notification.getStatus() == NotificationStatus.EXPIRED) {
                notification.reserve(scheduledAt);
            } else {
                notification.updateScheduledAt(scheduledAt);
            }
        }
    }

    private void updateAnniversaryNotifications(Long memberId, boolean enabled, MemberSetting memberSetting) {

        List<Notification> notifications = notificationRepository.findAllWithAnniversary(memberId, List.of(NotificationType.ANNIVERSARY_D7, NotificationType.ANNIVERSARY_DDAY), List.of(NotificationStatus.SCHEDULED, NotificationStatus.EXPIRED));
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        for(Notification notification : notifications) {

            if(!enabled) {
                notification.updateSettingEnabled(false);
                continue;
            }

            Anniversary anniversary = notification.getAnniversary();

            if (anniversary == null) {
                continue;
            }

            LocalDate nextOccurrence = resolveNextOccurrence(anniversary, today);

            if(nextOccurrence == null) {
                continue;
            }

            LocalDateTime scheduledAt;

            if(notification.getNotificationType() == NotificationType.ANNIVERSARY_D7) {
                scheduledAt = nextOccurrence.minusDays(7).atTime(memberSetting.getRegularNotificationTime());
            } else {
                scheduledAt = nextOccurrence.atTime(memberSetting.getRegularNotificationTime());
            }

            notification.updateSettingEnabled(true);

            if (!scheduledAt.isAfter(now)) {
                notification.expire();
                continue;
            }

            if (notification.getStatus() == NotificationStatus.EXPIRED) {
                notification.reserve(scheduledAt);
            } else {
                notification.updateScheduledAt(scheduledAt);
            }
        }
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
        return thisYear.isBefore(today) ? anniversaryDate.withYear(today.getYear() + 1) : thisYear;
    }
}
