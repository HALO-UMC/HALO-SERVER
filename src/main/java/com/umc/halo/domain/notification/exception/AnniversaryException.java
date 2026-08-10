package com.umc.halo.domain.notification.exception;

import com.umc.halo.domain.notification.exception.code.AnniversaryErrorCode;
import com.umc.halo.global.apiPayload.exception.ProjectException;

public class AnniversaryException extends ProjectException {

    public AnniversaryException(AnniversaryErrorCode errorCode) {
        super(errorCode);
    }

    public AnniversaryException(AnniversaryErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}