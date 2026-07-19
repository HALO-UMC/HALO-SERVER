package com.umc.halo.domain.notification.exception;

import com.umc.halo.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AnniversarySuccessCode implements BaseSuccessCode {

    ANNIVERSARY_LIST_SUCCESS(HttpStatus.OK, "ANNIVERSARY200_1", "기념일 목록 조회에 성공했습니다."),
    ANNIVERSARY_CREATE_SUCCESS(HttpStatus.CREATED, "ANNIVERSARY201_1", "기념일이 성공적으로 생성되었습니다."),
    ANNIVERSARY_UPDATE_SUCCESS(HttpStatus.OK, "ANNIVERSARY200_2", "기념일이 성공적으로 수정되었습니다."),
    ANNIVERSARY_DELETE_SUCCESS(HttpStatus.OK, "ANNIVERSARY200_3", "기념일이 성공적으로 삭제되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
