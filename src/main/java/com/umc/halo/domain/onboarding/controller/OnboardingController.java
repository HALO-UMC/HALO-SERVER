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
import com.umc.halo.domain.onboarding.dto.OnboardingReqDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/onboarding")
public class OnboardingController implements OnboardingControllerDocs {

    private final OnboardingService onboardingService;
    // 닉네임 중복 확인
    @GetMapping("/nickname/check")
    public ApiResponse<OnboardingResDTO.NicknameCheck> checkNickname(
            @RequestParam String nickname
    ) {
        BaseSuccessCode code = OnboardingSuccessCode.NICKNAME_AVAILABLE;
        return ApiResponse.onSuccess(code, onboardingService.checkNickname(nickname));
    }

    // 온보딩 정보 저장
    @PostMapping
    public ApiResponse<OnboardingResDTO.Save> saveOnboarding(
            @AuthenticationPrincipal Long memberId,
            @RequestBody OnboardingReqDTO.Save request
    ) {
        BaseSuccessCode code = OnboardingSuccessCode.SAVE_SUCCESS;
        return ApiResponse.onSuccess(code, onboardingService.saveOnboarding(memberId, request));
    }

    // 온보딩 진행 상태 조회
    @GetMapping("/status")
    public ApiResponse<OnboardingResDTO.Status> getStatus(
            @AuthenticationPrincipal Long memberId
    ) {
        BaseSuccessCode code = OnboardingSuccessCode.STATUS_SUCCESS;
        return ApiResponse.onSuccess(code, onboardingService.getStatus(memberId));
    }
}