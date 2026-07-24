package com.umc.halo.domain.exhibition.exception.code;

import com.umc.halo.global.apiPayload.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExhibitionSuccessCode implements BaseSuccessCode {

    MAIN_SUCCESS(HttpStatus.OK, "EXHIBITION200_1", "테마함 조회를 성공했습니다."),
    CHAPTERS_SUCCESS(HttpStatus.OK, "EXHIBITION200_2", "스토리북 감상 조회를 성공했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}