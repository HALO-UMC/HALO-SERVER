package com.umc.halo.domain.term.exception;

import com.umc.halo.global.apiPayload.code.BaseErrorCode;
import com.umc.halo.global.apiPayload.exception.ProjectException;

public class TermException extends ProjectException {
    public TermException(BaseErrorCode errorCode) {
        super(errorCode);
    }

    public TermException(BaseErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}