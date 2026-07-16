package com.umc.halo.domain.content.storybook.exception;

import com.umc.halo.global.apiPayload.code.*;
import com.umc.halo.global.apiPayload.exception.*;

public class StorybookException extends ProjectException {
    public StorybookException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
