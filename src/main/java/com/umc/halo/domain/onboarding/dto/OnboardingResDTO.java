package com.umc.halo.domain.onboarding.dto;

import lombok.Builder;

public class OnboardingResDTO {

    @Builder
    public record NicknameCheck(
            Boolean isAvailable
    ) {}
}