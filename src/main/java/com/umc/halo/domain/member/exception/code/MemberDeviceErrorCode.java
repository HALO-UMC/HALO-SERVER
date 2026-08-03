package com.umc.halo.domain.member.exception.code;

import com.umc.halo.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberDeviceErrorCode implements BaseErrorCode {

    DEVICE_NOT_FOUND(HttpStatus.NOT_FOUND,
            "MEMBER_DEVICE_400_1",
            "등록된 기기를 찾을 수 없습니다."
    ),
    INVALID_DEVICE_TYPE(HttpStatus.BAD_REQUEST,
            "MEMBER_DEVICE_400_2",
            "올바르지 않은 기기 타입입니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
