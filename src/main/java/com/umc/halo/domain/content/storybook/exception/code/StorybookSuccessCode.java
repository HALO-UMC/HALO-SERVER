package com.umc.halo.domain.content.storybook.exception.code;

import com.umc.halo.global.apiPayload.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StorybookSuccessCode implements BaseSuccessCode {
    GET_STORYBOOK_LIST(HttpStatus.OK, "STORYBOOK200_1", "스토리북 목록을 성공적으로 조회했습니다."),
    GET_STORYBOOK_DETAIL(HttpStatus.OK, "STORYBOOK200_2", "스토리북 상세를 성공적으로 조회했습니다."),
    START_STORYBOOK(HttpStatus.CREATED, "STORYBOOK201_1", "스토리북을 시작했습니다."),
    GET_RECOMMENDED_STORYBOOKS(HttpStatus.OK, "STORYBOOK200_3", "추천 스토리북을 성공적으로 조회했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
