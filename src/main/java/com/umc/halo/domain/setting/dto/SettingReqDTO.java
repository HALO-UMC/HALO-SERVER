package com.umc.halo.domain.setting.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalTime;

public class SettingReqDTO {

    @Builder
    public record UpdateNotificationSettings(
            @NotNull
            Boolean regularNotificationEnabled,

            LocalTime regularNotificationTime,

            @NotNull
            Boolean todayChapterNotificationEnabled,

            @NotNull
            Boolean retentionNotificationEnabled,

            @NotNull
            Boolean anniversaryNotificationEnabled
    ) {}
}
