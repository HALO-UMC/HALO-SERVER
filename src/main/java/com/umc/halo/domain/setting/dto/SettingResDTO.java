package com.umc.halo.domain.setting.dto;

import lombok.Builder;

import java.time.LocalTime;

public class SettingResDTO {

    @Builder
    public record NotificationSetting(
            Boolean regularNotificationEnabled,
            LocalTime regularNotificationTime,
            Boolean todayChapterNotificationEnabled,
            Boolean retentionNotificationEnabled,
            Boolean anniversaryNotificationEnabled,
            Boolean isAllNotificationEnabled
    ) {}
}
