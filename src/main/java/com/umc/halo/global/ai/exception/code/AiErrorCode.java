package com.umc.halo.global.ai.exception.code;

import com.umc.halo.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AiErrorCode implements BaseErrorCode {

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
