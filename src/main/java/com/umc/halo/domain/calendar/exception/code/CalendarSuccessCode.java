package com.umc.halo.domain.calendar.exception.code;

import com.umc.halo.global.apiPayload.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CalendarSuccessCode implements BaseSuccessCode {

    MAIN_SUCCESS(HttpStatus.OK,
            "CALENDAR200_1",
            "캘린더 메인 조회를 성공했습니다."),
    DAILY_SUCCESS(HttpStatus.OK,
            "CALENDAR200_2",
            "일별 기록 조회를 성공했습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}