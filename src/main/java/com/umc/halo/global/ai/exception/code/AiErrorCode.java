package com.umc.halo.global.ai.exception.code;

import com.umc.halo.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AiErrorCode implements BaseErrorCode {

    AI_RATE_LIMIT_EXCEEDED(
            HttpStatus.TOO_MANY_REQUESTS,
            "AI429_1",
            "AI 요청 횟수를 초과했습니다. 한도 갱신 후 다시 시도해주세요."
    ),

    AI_RESPONSE_EMPTY(
            HttpStatus.BAD_GATEWAY,
            "AI502_1",
            "AI 응답이 없습니다."
    ),

    AI_RESPONSE_INVALID(
            HttpStatus.BAD_GATEWAY,
            "AI502_2",
            "AI 응답 형식이 올바르지 않습니다."
    ),

    AI_GENERATE_FAILED(
            HttpStatus.BAD_GATEWAY,
            "AI502_3",
            "AI 요약 생성에 실패했습니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
