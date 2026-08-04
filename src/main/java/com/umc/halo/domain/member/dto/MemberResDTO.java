package com.umc.halo.domain.member.dto;

import com.umc.halo.domain.member.enums.Gender;
import com.umc.halo.domain.member.enums.Provider;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MemberResDTO {

    @Builder
    public record Login(
            String accessToken,
            String refreshToken,
            Boolean isNewUser,
            Boolean onboardingCompleted,
            Boolean termsAgreed
    ) {
    }

    @Builder
    public record TokenReissue(
            String accessToken,
            String refreshToken,
            Boolean onboardingCompleted,
            Boolean termsAgreed
    ) {
    }

    @Builder
    public record MyInfo(
            Long memberId,
            String name,
            Gender gender,
            LocalDate birthDate,
            Provider provider,
            Boolean onboardingCompleted,
            String characterImageUrl,
            LocalDateTime createdAt
    ) {
    }
}
