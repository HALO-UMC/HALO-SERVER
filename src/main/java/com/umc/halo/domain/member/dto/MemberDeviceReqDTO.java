package com.umc.halo.domain.member.dto;

import com.umc.halo.domain.member.enums.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MemberDeviceReqDTO {

    // 등록
    public record Register(

            @NotBlank(message = "fcmToken은 필수입니다.")
            String fcmToken,

            @NotNull(message = "deviceType은 필수입니다.")
            DeviceType deviceType,

            @NotBlank(message = "deviceIdentifier는 필수입니다.")
            String deviceIdentifier
    ) {}
}
