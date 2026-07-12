package com.umc.halo.domain.onboarding.exception.code;

import com.umc.halo.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OnboardingErrorCode implements BaseErrorCode {

    // 400
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST,
            "ONBOARDING400_1",
            "닉네임은 2~10자, 특수문자를 사용할 수 없습니다."),
    PARENT_TAG_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST,
            "ONBOARDING400_2",
            "부모 성향 태그는 최대 3개까지 선택할 수 있습니다."),
    MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST,
            "ONBOARDING400_3",
            "필수 입력값이 누락되었습니다."),
    GOAL_TAG_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST,
            "ONBOARDING400_4",
            "원하는 관계 방향은 최대 2개까지 선택할 수 있습니다."),
    INVALID_BIRTH_DATE(HttpStatus.BAD_REQUEST,
            "ONBOARDING400_5",
            "생년월일 형식이 올바르지 않습니다."),

    // 404
    TAG_NOT_FOUND(HttpStatus.NOT_FOUND,
            "ONBOARDING404_1",
            "존재하지 않는 태그입니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}