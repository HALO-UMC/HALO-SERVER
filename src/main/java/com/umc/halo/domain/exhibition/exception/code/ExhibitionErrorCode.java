package com.umc.halo.domain.exhibition.exception.code;

import com.umc.halo.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExhibitionErrorCode implements BaseErrorCode {

    NOT_COMPLETED(HttpStatus.FORBIDDEN, "EXHIBITION403_1", "아직 완료되지 않은 스토리북입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "EXHIBITION404_1", "존재하지 않거나 접근 권한이 없는 전시 기록입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}