package com.umc.halo.domain.setting.exception;

import com.umc.halo.global.apiPayload.code.BaseErrorCode;
import com.umc.halo.global.apiPayload.exception.ProjectException;

public class SettingException extends ProjectException {
    public SettingException(BaseErrorCode errorCode) {
        super(errorCode);
    }

    public SettingException(BaseErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
