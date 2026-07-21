package com.umc.halo.domain.image.exception.code;

import com.umc.halo.global.apiPayload.code.*;
import lombok.*;
import org.springframework.http.*;

@Getter
@RequiredArgsConstructor
public enum ImageErrorCode implements BaseErrorCode {

    INVALID_CONTENT_TYPE(HttpStatus.BAD_REQUEST, "IMAGE400_1", "지원하지 않는 파일 형식입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}