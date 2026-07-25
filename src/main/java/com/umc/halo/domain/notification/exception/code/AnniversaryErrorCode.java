package com.umc.halo.domain.notification.exception.code;

import com.umc.halo.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AnniversaryErrorCode implements BaseErrorCode {

    ANNIVERSARY_NOT_FOUND(HttpStatus.NOT_FOUND, "ANNIVERSARY404_1", "존재하지 않는 일정입니다."),
    ANNIVERSARY_ACCESS_DENIED(HttpStatus.FORBIDDEN, "ANNIVERSARY403_1", "해당 기념일에 대한 권한이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
