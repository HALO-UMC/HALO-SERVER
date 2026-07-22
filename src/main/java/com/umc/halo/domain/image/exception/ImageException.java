package com.umc.halo.domain.image.exception;

import com.umc.halo.global.apiPayload.code.*;
import com.umc.halo.global.apiPayload.exception.*;

public class ImageException extends ProjectException {
    public ImageException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}