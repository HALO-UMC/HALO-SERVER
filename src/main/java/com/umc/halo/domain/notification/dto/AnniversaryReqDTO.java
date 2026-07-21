package com.umc.halo.domain.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public class AnniversaryReqDTO {

    public record Create(
            @NotBlank(message = "기념일명은 필수입니다.")
            @Size(max = 20, message = "기념일명은 20자 이하로 입력해주세요.")
            String title,

            @NotNull(message = "날짜는 필수입니다.")
            LocalDate anniversaryDate,

            @NotNull(message = "D-7 알림 여부는 필수입니다.")
            Boolean sevenDaysAlarmEnabled,

            @NotNull(message = "당일 알림 여부는 필수입니다.")
            Boolean dayAlarmEnabled,

            @Size(max = 255, message = "메모는 255자 이하로 입력해주세요.")
            String memo
    ) {}

    public record Update(
            @NotBlank(message = "기념일명은 필수입니다.")
            @Size(max = 20, message = "기념일명은 20자 이하로 입력해주세요.")
            String title,

            @NotNull(message = "날짜는 필수입니다.")
            LocalDate anniversaryDate,

            @NotNull(message = "D-7 알림 여부는 필수입니다.")
            Boolean sevenDaysAlarmEnabled,

            @NotNull(message = "당일 알림 여부는 필수입니다.")
            Boolean dayAlarmEnabled,

            @Size(max = 255, message = "메모는 255자 이하로 입력해주세요.")
            String memo
    ) {}

    public record Delete(
            @NotEmpty(message = "삭제할 기념일 ID 목록은 필수입니다.")
            List<Long> anniversaryIds
    ) {}
}
