package com.umc.halo.domain.member.exception.code;

import com.umc.halo.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {

    // 400
    UNSUPPORTED_PROVIDER(HttpStatus.BAD_REQUEST,
            "AUTH400_1",
            "지원하지 않는 소셜 로그인 제공자입니다."),

    // 401
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED,
            "AUTH401_2",
            "refreshToken이 만료되었거나 유효하지 않습니다."),
    INVALID_PROVIDER_TOKEN(HttpStatus.UNAUTHORIZED,
            "AUTH401_3",
            "소셜 인증 토큰 검증에 실패했습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
