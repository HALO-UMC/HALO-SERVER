package com.umc.halo.domain.content.storybook.apiPayload;

import com.umc.halo.global.apiPayload.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum HomeSuccessCode implements BaseSuccessCode {

    GET_HOME(HttpStatus.OK, "HOME200_1", "홈 화면을 성공적으로 조회했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}