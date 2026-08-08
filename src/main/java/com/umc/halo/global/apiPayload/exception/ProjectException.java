package com.umc.halo.global.apiPayload.exception;

import com.umc.halo.global.apiPayload.code.BaseErrorCode;

import lombok.Getter;

@Getter
public class ProjectException extends RuntimeException {
    private final BaseErrorCode errorCode;

    public ProjectException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ProjectException(BaseErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
