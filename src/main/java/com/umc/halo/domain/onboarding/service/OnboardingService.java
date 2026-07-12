package com.umc.halo.domain.onboarding.service;

import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.onboarding.dto.OnboardingResDTO;
import com.umc.halo.domain.onboarding.exception.OnboardingException;
import com.umc.halo.domain.onboarding.exception.code.OnboardingErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.tag.entity.MemberTag;
import com.umc.halo.domain.tag.entity.Tag;
import com.umc.halo.domain.tag.repository.MemberTagRepository;
import com.umc.halo.domain.tag.repository.TagRepository;
import com.umc.halo.domain.onboarding.dto.OnboardingReqDTO;

import java.util.regex.Pattern;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;
    private final MemberTagRepository memberTagRepository;

    private static final Pattern NICKNAME_PATTERN =
            Pattern.compile("^[가-힣a-zA-Z0-9]{2,10}$");
    private static final int PARENT_TAG_MAX = 3;
    private static final int GOAL_TAG_MIN = 1;
    private static final int GOAL_TAG_MAX = 2;

    @Transactional(readOnly = true)
    public OnboardingResDTO.NicknameCheck checkNickname(String nickname) {

        if (nickname == null || !NICKNAME_PATTERN.matcher(nickname).matches()) {
            throw new OnboardingException(OnboardingErrorCode.INVALID_NICKNAME);
        }

        // 중복 조회
        boolean available = !memberRepository.existsByName(nickname);

        return OnboardingResDTO.NicknameCheck.builder()
                .isAvailable(available)
                .build();
    }

    // 온보딩 정보 저장 이어서 하는 작업용
    @Transactional
    public OnboardingResDTO.Save saveOnboarding(Long memberId, OnboardingReqDTO.Save request) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new OnboardingException(OnboardingErrorCode.MISSING_REQUIRED_FIELD));

        Integer step = request.step();
        if (step == null || step < 1 || step > 5) {
            throw new OnboardingException(OnboardingErrorCode.MISSING_REQUIRED_FIELD);
        }

        switch (step) {
            case 1 -> {
                if (request.name() == null) {
                    throw new OnboardingException(OnboardingErrorCode.MISSING_REQUIRED_FIELD);
                }
                member.updateName(request.name());
            }
            case 2 -> {
                if (request.gender() == null || request.birthDate() == null) {
                    throw new OnboardingException(OnboardingErrorCode.MISSING_REQUIRED_FIELD);
                }
                member.updateGenderAndBirthDate(request.gender(), request.birthDate());
            }
            case 3 -> {
                List<Long> tagIds = request.parentPersonalityTagIds();
                if (tagIds == null || tagIds.isEmpty()) {
                    throw new OnboardingException(OnboardingErrorCode.MISSING_REQUIRED_FIELD);
                }
                if (tagIds.size() > PARENT_TAG_MAX) {
                    throw new OnboardingException(OnboardingErrorCode.PARENT_TAG_LIMIT_EXCEEDED);
                }
                saveTags(member, tagIds);
            }
            case 4 -> {
                if (request.currentRelationStateTagId() == null) {
                    throw new OnboardingException(OnboardingErrorCode.MISSING_REQUIRED_FIELD);
                }
                saveTags(member, List.of(request.currentRelationStateTagId()));
            }
            case 5 -> {
                List<Long> tagIds = request.goalRelationshipTagIds();
                if (tagIds == null || tagIds.size() < GOAL_TAG_MIN) {
                    throw new OnboardingException(OnboardingErrorCode.MISSING_REQUIRED_FIELD);
                }
                if (tagIds.size() > GOAL_TAG_MAX) {
                    throw new OnboardingException(OnboardingErrorCode.GOAL_TAG_LIMIT_EXCEEDED);
                }
                saveTags(member, tagIds);
            }
        }

        member.updateOnboardingStep(step);

        if (step == 5) {
            member.completeOnboarding();
        }

        return OnboardingResDTO.Save.builder()
                .onboardingStep(member.getOnboardingStep())
                .onboardingCompleted(member.getOnboardingCompleted())
                .build();
    }

    private void saveTags(Member member, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            Tag tag = tagRepository.findById(tagId)
                    .orElseThrow(() -> new OnboardingException(OnboardingErrorCode.TAG_NOT_FOUND));
            MemberTag memberTag = MemberTag.builder()
                    .member(member)
                    .tag(tag)
                    .build();
            memberTagRepository.save(memberTag);
        }
    }
}
