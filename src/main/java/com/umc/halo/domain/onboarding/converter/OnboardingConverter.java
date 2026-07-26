package com.umc.halo.domain.onboarding.converter;

import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.onboarding.dto.OnboardingResDTO;

import java.util.List;

public class OnboardingConverter {

    public static OnboardingResDTO.NicknameCheck toNicknameCheck(boolean available) {
        return OnboardingResDTO.NicknameCheck.builder()
                .isAvailable(available)
                .build();
    }

    public static OnboardingResDTO.Save toSave(Member member) {
        return OnboardingResDTO.Save.builder()
                .onboardingStep(member.getOnboardingStep())
                .onboardingCompleted(member.getOnboardingCompleted())
                .build();
    }

    public static OnboardingResDTO.Status toStatus(Member member, OnboardingResDTO.SavedData savedData) {
        return OnboardingResDTO.Status.builder()
                .onboardingCompleted(member.getOnboardingCompleted())
                .currentStep(member.getOnboardingStep())
                .savedData(savedData)
                .build();
    }

    public static OnboardingResDTO.SavedData toSavedData(
            Member member,
            List<Long> parentPersonalityTagIds,
            Long currentRelationStateTagId,
            List<Long> goalRelationshipTagIds
    ) {
        return OnboardingResDTO.SavedData.builder()
                .name(member.getName())
                .gender(member.getGender())
                .birthDate(member.getBirthDate())
                .parentPersonalityTagIds(parentPersonalityTagIds)
                .currentRelationStateTagId(currentRelationStateTagId)
                .goalRelationshipTagIds(goalRelationshipTagIds)
                .build();
    }
}