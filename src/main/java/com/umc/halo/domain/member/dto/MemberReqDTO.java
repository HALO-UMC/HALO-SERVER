package com.umc.halo.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MemberReqDTO {

    // 로그인
    public record Login(
            @NotNull
            String provider,
            @NotBlank
            String providerToken
    ) {}

    // 토큰 재발급
    public record TokenReissue(
            @NotBlank
            String refreshToken
    ) {}
}
