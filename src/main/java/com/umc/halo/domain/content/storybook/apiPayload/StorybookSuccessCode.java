package com.umc.halo.domain.content.storybook.apiPayload;

import com.umc.halo.global.apiPayload.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StorybookSuccessCode implements BaseSuccessCode {

    GET_STORYBOOK_DETAIL(HttpStatus.OK, "STORYBOOK200_2", "스토리북 상세를 성공적으로 조회했습니다."),
    START_STORYBOOK(HttpStatus.CREATED, "STORYBOOK201_1", "스토리북을 성공적으로 시작했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}