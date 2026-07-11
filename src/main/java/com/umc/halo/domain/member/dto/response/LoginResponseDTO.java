package com.umc.halo.domain.member.dto.response;

import lombok.Builder;

public class LoginResponseDTO {

    @Builder
    public record LoginResponse (
            String accessToken,
            String refreshToken,
            Boolean isNewUser,
            Boolean onboardingCompleted
    ) {}
}
