package com.umc.halo.domain.setting.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalTime;

public class SettingReqDTO {

    @Builder
    public record UpdateNotificationSettings(
            @NotNull
            Boolean regularNotificationEnabled,

            @NotNull
            LocalTime regularNotificationTime,

            @NotNull
            Boolean todayChapterNotificationEnabled,

            @NotNull
            Boolean retentionNotificationEnabled,

            @NotNull
            Boolean anniversaryNotificationEnabled
    ) {}

    @Builder
    public record UpdateBgmSettings(
            Long bgmId,

            @NotNull
            Boolean bgmEnabled,

            @NotNull
            @Min(value = 0, message = "BGM 볼륨 값이 올바르지 않습니다.")
            @Max(value = 100, message = "BGM 볼륨 값이 올바르지 않습니다.")
            Integer bgmVolume
    ) {}
}
