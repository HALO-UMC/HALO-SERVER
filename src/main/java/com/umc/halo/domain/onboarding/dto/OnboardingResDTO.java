package com.umc.halo.domain.onboarding.dto;

import lombok.Builder;
import com.umc.halo.domain.member.enums.Gender;

public class OnboardingResDTO {

    @Builder
    public record NicknameCheck(
            Boolean isAvailable
    ) {}

    @Builder
    public record Save(
            Integer onboardingStep,
            Boolean onboardingCompleted
    ) {}

    // 온보딩 진행 상태 조회하기
    @Builder
    public record Status(
            Boolean onboardingCompleted,
            Integer currentStep,        // 시작 전 null
            SavedData savedData         // 시작 전 null
    ) {}

    // 지금까지 저장된 값
    @Builder
    public record SavedData(
            String name,
            Gender gender,
            java.time.LocalDate birthDate,
            java.util.List<Long> parentPersonalityTagIds,
            Long currentRelationStateTagId,
            java.util.List<Long> goalRelationshipTagIds
    ) {}
}