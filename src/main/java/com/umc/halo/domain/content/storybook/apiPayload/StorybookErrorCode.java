package com.umc.halo.domain.content.storybook.apiPayload;

import com.umc.halo.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StorybookErrorCode implements BaseErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "STORYBOOK404_1", "존재하지 않는 스토리북입니다."),
    ALREADY_STARTED(HttpStatus.CONFLICT, "STORYBOOK409_2", "이미 시작한 스토리북입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}