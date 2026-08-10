package com.umc.halo.domain.onboarding.exception;

import com.umc.halo.global.apiPayload.code.BaseErrorCode;
import com.umc.halo.global.apiPayload.exception.ProjectException;

public class OnboardingException extends ProjectException {
    public OnboardingException(BaseErrorCode errorCode) {
        super(errorCode);
    }

    public OnboardingException(BaseErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}