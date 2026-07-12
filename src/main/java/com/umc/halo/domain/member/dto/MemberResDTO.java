package com.umc.halo.domain.member.dto;

import lombok.Builder;

public class MemberResDTO {

    @Builder
    public record Login(
            String accessToken,
            String refreshToken,
            Boolean isNewUser,
            Boolean onboardingCompleted
    ) {}

    @Builder
    public record TokenReissue(
            String accessToken,
            String refreshToken
    ) {}
}
