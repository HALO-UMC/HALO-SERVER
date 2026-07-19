package com.umc.halo.domain.setting.exception.code;

import com.umc.halo.global.apiPayload.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SettingSuccessCode implements BaseSuccessCode {

    NOTIFICATION_SETTING_GET_SUCCESS(HttpStatus.OK,
            "NOTIFICATION200_1",
            "알림 설정을 성공적으로 조회했습니다."),
    NOTIFICATION_SETTING_UPDATE_SUCCESS(HttpStatus.OK,
            "NOTIFICATION200_2",
            "알림 설정을 성공적으로 수정했습니다."),
    BGM_SETTING_GET_SUCCESS(HttpStatus.OK,
            "BGM200_1",
            "BGM 설정 조회를 성공했습니다."),
    BGM_SETTING_UPDATE_SUCCESS(HttpStatus.OK,
            "BGM200_2",
            "BGM 설정 수정을 성공했습니다."),
    BGM_LIST_GET_SUCCESS(HttpStatus.OK,
            "BGM200_3",
            "BGM 목록 조회를 성공했습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
