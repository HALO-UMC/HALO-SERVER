package com.umc.halo.domain.setting.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalTime;

public class SettingReqDTO {

    @Builder
    public record UpdateNotificationSettings(
            @NotNull(message = "전체 알림 설정 여부는 필수입니다.")
            Boolean isAllNotificationEnabled,

            @NotNull(message = "정기 알림 시간은 필수입니다.")
            LocalTime regularNotificationTime,

            @NotNull(message = "오늘의 장 알림 설정 여부는 필수입니다.")
            Boolean todayChapterNotificationEnabled,

            @NotNull(message = "리텐션 알림 설정 여부는 필수입니다.")
            Boolean retentionNotificationEnabled,

            @NotNull(message = "기념일 알림 설정 여부는 필수입니다.")
            Boolean anniversaryNotificationEnabled
    ) {}

    @Builder
    public record UpdateBgmSettings(
            Long bgmId,
            Boolean bgmEnabled,
            Integer bgmVolume
    ) {}
}
