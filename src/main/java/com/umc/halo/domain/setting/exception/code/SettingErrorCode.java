package com.umc.halo.domain.setting.exception.code;

import com.umc.halo.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SettingErrorCode implements BaseErrorCode {

    INVALID_REGULAR_NOTIFICATION_TIME(HttpStatus.BAD_REQUEST,
            "NOTIFICATION400_1",
            "정기 알림 시간 형식이 올바르지 않습니다."),
    BGM_ENABLED_REQUIRED(HttpStatus.BAD_REQUEST,
            "BGM400_1",
            "BGM 사용 여부는 필수입니다."),
    BGM_VOLUME_REQUIRED(HttpStatus.BAD_REQUEST,
            "BGM400_2",
            "BGM 볼륨은 필수입니다."),
    INVALID_BGM_VOLUME(HttpStatus.BAD_REQUEST,
            "BGM400_3",
            "BGM 볼륨 값이 올바르지 않습니다."),
    BGM_NOT_FOUND(HttpStatus.NOT_FOUND,
            "BGM400_4",
            "존재하지 않는 BGM입니다."),
    SETTING_NOT_FOUND(HttpStatus.NOT_FOUND,
            "SETTING404_1",
            "설정을 찾을 수 없습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
