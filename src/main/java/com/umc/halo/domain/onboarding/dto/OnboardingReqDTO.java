package com.umc.halo.domain.onboarding.dto;

import com.umc.halo.domain.member.enums.Gender;

import java.time.LocalDate;
import java.util.List;

public class OnboardingReqDTO {

    public record Save(
            Integer step, // 현재 저장 단계 (필수)

            String name,  // step 1
            Gender gender, // step 2
            LocalDate birthDate, // step 2

            List<Long> parentPersonalityTagIds,   // step 3 (최대 3)
            Long currentRelationStateTagId, // step 4 (1개)
            List<Long> goalRelationshipTagIds // step 5 (최소 1, 최대 2)
    ) {}
}