package com.umc.halo.domain.setting.dto;

import lombok.Builder;

import java.time.LocalTime;
import java.util.List;

public class SettingResDTO {

    @Builder
    public record NotificationSettings(
            Boolean isAllNotificationEnabled,
            LocalTime regularNotificationTime,
            Boolean todayChapterNotificationEnabled,
            Boolean retentionNotificationEnabled,
            Boolean anniversaryNotificationEnabled
    ) {}

    @Builder
    public record BgmSettings(
            Long bgmId,
            Boolean bgmEnabled,
            Integer bgmVolume
    ) {}

    @Builder
    public record BgmInfo(
            Long bgmId,
            String title,
            String fileName,
            String imageName
    ) {}

    @Builder
    public record Bgms(
            List<BgmInfo> bgms
    ) {}
}
