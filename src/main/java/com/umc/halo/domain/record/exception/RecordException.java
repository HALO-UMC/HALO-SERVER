package com.umc.halo.domain.record.exception;

import com.umc.halo.global.apiPayload.code.*;
import com.umc.halo.global.apiPayload.exception.*;

public class RecordException extends ProjectException {
    public RecordException(BaseErrorCode errorCode) {
        super(errorCode);
    }

    public RecordException(BaseErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
