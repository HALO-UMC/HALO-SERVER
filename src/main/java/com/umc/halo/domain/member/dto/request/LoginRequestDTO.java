package com.umc.halo.domain.member.dto.request;

import com.umc.halo.domain.member.enums.Provider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LoginRequestDTO {

    // 로그인
    public record Login (
            @NotNull
            String provider,
            @NotBlank
            String providerToken
    ) {}
}
