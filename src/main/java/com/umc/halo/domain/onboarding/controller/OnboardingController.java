package com.umc.halo.domain.onboarding.controller;

import com.umc.halo.domain.onboarding.controller.docs.OnboardingControllerDocs;
import com.umc.halo.domain.onboarding.dto.OnboardingResDTO;
import com.umc.halo.domain.onboarding.exception.code.OnboardingSuccessCode;
import com.umc.halo.domain.onboarding.service.OnboardingService;
import com.umc.halo.global.apiPayload.ApiResponse;
import com.umc.halo.global.apiPayload.code.BaseSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/onboarding")
public class OnboardingController implements OnboardingControllerDocs {

    private final OnboardingService onboardingService;

    @GetMapping("/nickname/check")
    public ApiResponse<OnboardingResDTO.NicknameCheck> checkNickname(
            @RequestParam String nickname
    ) {
        BaseSuccessCode code = OnboardingSuccessCode.NICKNAME_AVAILABLE;
        return ApiResponse.onSuccess(code, onboardingService.checkNickname(nickname));
    }
}