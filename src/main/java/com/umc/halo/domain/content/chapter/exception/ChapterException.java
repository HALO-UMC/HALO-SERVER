package com.umc.halo.domain.content.chapter.exception;

import com.umc.halo.global.apiPayload.code.*;
import com.umc.halo.global.apiPayload.exception.*;

public class ChapterException extends ProjectException {
    public ChapterException(BaseErrorCode errorCode) {
        super(errorCode);
    }

    public ChapterException(BaseErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
