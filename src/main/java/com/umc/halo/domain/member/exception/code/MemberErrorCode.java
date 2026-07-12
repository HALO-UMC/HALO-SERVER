package com.umc.halo.domain.member.exception.code;

import com.umc.halo.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND,
            "MEMBER404_1",
            "존재하지 않는 회원입니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
