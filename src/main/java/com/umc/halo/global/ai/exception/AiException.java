package com.umc.halo.global.ai.exception;

import com.umc.halo.global.apiPayload.code.BaseErrorCode;
import com.umc.halo.global.apiPayload.exception.ProjectException;

public class AiException extends ProjectException {
    public AiException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
