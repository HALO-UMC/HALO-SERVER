package com.umc.halo.domain.onboarding.converter;

import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.onboarding.dto.OnboardingResDTO;
import com.umc.halo.domain.tag.entity.Tag;
import java.util.List;
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
    public static OnboardingResDTO.TagInfo toTagInfo(Tag tag) {
        return new OnboardingResDTO.TagInfo(
                tag.getId(),
                tag.getTitle(),
                tag.getSubtitle(),
                tag.getDescription()
        );
    }

    public static OnboardingResDTO.TagList toTagList(
            List<Tag> parentPersonalityTags,
            List<Tag> currentRelationStateTags,
            List<Tag> goalRelationshipTags
    ) {
        return new OnboardingResDTO.TagList(
                parentPersonalityTags.stream().map(OnboardingConverter::toTagInfo).toList(),
                currentRelationStateTags.stream().map(OnboardingConverter::toTagInfo).toList(),
                goalRelationshipTags.stream().map(OnboardingConverter::toTagInfo).toList()
        );
    }
}