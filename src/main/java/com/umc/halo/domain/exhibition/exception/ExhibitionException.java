package com.umc.halo.domain.exhibition.exception;

import com.umc.halo.global.apiPayload.code.BaseErrorCode;
import com.umc.halo.global.apiPayload.exception.ProjectException;

public class ExhibitionException extends ProjectException {
    public ExhibitionException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}