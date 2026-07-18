package com.umc.halo.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MemberReqDTO {

    // 로그인
    public record Login(
            @NotNull(message = "provider는 필수입니다.")
            String provider,
            @NotBlank(message = "providerToken은 필수입니다.")
            String providerToken
    ) {}

    // 토큰 재발급
    public record TokenReissue(
            @NotBlank(message = "refreshToken은 필수입니다.")
            String refreshToken
    ) {}
}
