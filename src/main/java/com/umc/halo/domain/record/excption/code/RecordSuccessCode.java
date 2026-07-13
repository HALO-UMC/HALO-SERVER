package com.umc.halo.domain.record.excption.code;

import com.umc.halo.global.apiPayload.code.*;
import org.springframework.http.*;

public enum RecordSuccessCode implements BaseSuccessCode{



    private final HttpStatus status;
    private final String code;
    private final String message;
}
