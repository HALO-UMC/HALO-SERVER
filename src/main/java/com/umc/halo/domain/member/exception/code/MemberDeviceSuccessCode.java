package com.umc.halo.domain.member.exception.code;

import com.umc.halo.global.apiPayload.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberDeviceSuccessCode implements BaseSuccessCode {

    DEVICE_REGISTER_SUCCESS(HttpStatus.OK,
            "MEMBER_DEVICE200_1",
            "기기가 성공적으로 등록되었습니다."
    ),
    DEVICE_DELETE_SUCCESS(HttpStatus.OK,
            "MEMBER_DEVICE200_2",
            "기기가 성공적으로 삭제되었습니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
