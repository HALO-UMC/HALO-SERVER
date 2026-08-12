package com.umc.halo.domain.term.exception.code;

import com.umc.halo.global.apiPayload.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TermSuccessCode implements BaseSuccessCode {

    LIST_SUCCESS(HttpStatus.OK,
            "TERM200_1",
            "약관 목록 조회 성공"),
    AGREE_SUCCESS(HttpStatus.OK,
            "TERM200_2",
            "약관 동의가 저장되었습니다."),
    AGREEMENT_STATUS_SUCCESS(HttpStatus.OK,
            "TERM200_3",
            "약관 동의 여부 조회 성공")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}