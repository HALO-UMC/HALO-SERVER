package com.umc.halo.global.ai.exception.code;

import com.umc.halo.global.apiPayload.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AiSuccessCode implements BaseSuccessCode {

    AI_SUMMARY_CREATED(HttpStatus.OK,
            "AI200_1",
                    "AI 요약이 생성되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
