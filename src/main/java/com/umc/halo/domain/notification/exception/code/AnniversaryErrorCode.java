package com.umc.halo.domain.notification.exception.code;

import com.umc.halo.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AnniversaryErrorCode implements BaseErrorCode {

    ANNIVERSARY_NOT_FOUND(HttpStatus.NOT_FOUND, "ANNIVERSARY404_1", "존재하지 않는 기념일입니다."),
    INVALID_LUNAR_DATE(HttpStatus.BAD_REQUEST, "ANNIVERSARY400_1", "존재하지 않는 음력 날짜입니다."),
    PAST_DATE_NOT_REPEATED(HttpStatus.BAD_REQUEST, "ANNIVERSARY400_2", "반복하지 않는 기념일은 지난 날짜로 등록할 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
