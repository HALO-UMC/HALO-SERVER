package com.umc.halo.domain.onboarding.exception.code;

import com.umc.halo.global.apiPayload.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OnboardingSuccessCode implements BaseSuccessCode {

    // 온보딩 정보 저장
    SAVE_SUCCESS(HttpStatus.OK,
            "ONBOARDING200_1",
            "온보딩 정보가 저장되었습니다."),
    // 온보딩 진행 상태 조회
    STATUS_SUCCESS(HttpStatus.OK,
            "ONBOARDING200_2",
            "온보딩 상태 조회 성공"),
    // 닉네임 중복 확인
    NICKNAME_AVAILABLE(HttpStatus.OK,
            "ONBOARDING200_3",
            "사용 가능한 닉네임입니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}