package com.umc.halo.domain.member.dto;

import lombok.Builder;

public class MemberResDTO {

    @Builder
    public record LoginResponse (
            String accessToken,
            String refreshToken,
            Boolean isNewUser,
            Boolean onboardingCompleted
    ) {}
}
