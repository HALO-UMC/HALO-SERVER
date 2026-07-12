package com.umc.halo.domain.onboarding.service;

import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.onboarding.dto.OnboardingResDTO;
import com.umc.halo.domain.onboarding.exception.OnboardingException;
import com.umc.halo.domain.onboarding.exception.code.OnboardingErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final MemberRepository memberRepository;

    private static final Pattern NICKNAME_PATTERN =
            Pattern.compile("^[가-힣a-zA-Z0-9]{2,10}$");

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
}