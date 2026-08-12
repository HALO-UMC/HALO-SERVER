package com.umc.halo.domain.onboarding.dto;

import com.umc.halo.domain.tag.enums.Subtitle;
import lombok.Builder;
import com.umc.halo.domain.member.enums.Gender;

import java.util.List;

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

    @Builder
    public record TagList(
            List<TagInfo> parentPersonalityTags,
            List<TagInfo> currentRelationStateTags,
            List<TagInfo> goalRelationshipTags
    ) {}

    @Builder
    public record TagInfo(
            Long tagId,
            String title,
            Subtitle subtitle,
            String description
    ) {}
}