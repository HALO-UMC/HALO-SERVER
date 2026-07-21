package com.umc.halo.domain.setting.dto;

import lombok.Builder;

import java.time.LocalTime;
import java.util.List;

public class SettingResDTO {

    @Builder
    public record NotificationSettings(
            Boolean regularNotificationEnabled,
            LocalTime regularNotificationTime,
            Boolean todayChapterNotificationEnabled,
            Boolean retentionNotificationEnabled,
            Boolean anniversaryNotificationEnabled,
            Boolean isAllNotificationEnabled
    ) {}

    @Builder
    public record BgmInfo(
            Long bgmId,
            String title,
            String fileName,
            String imgName
    ) {}

    @Builder
    public record Bgms(
            List<BgmInfo> bgms
    ) {}
}
