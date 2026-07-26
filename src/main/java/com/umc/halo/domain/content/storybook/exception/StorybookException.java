package com.umc.halo.domain.content.storybook.exception;

import com.umc.halo.global.apiPayload.code.BaseErrorCode;
import com.umc.halo.global.apiPayload.exception.ProjectException;

public class StorybookException extends ProjectException {
    public StorybookException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
