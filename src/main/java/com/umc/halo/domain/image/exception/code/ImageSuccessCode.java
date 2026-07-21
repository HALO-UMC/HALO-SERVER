package com.umc.halo.domain.image.exception.code;

import com.umc.halo.global.apiPayload.code.*;
import lombok.*;
import org.springframework.http.*;

@Getter
@RequiredArgsConstructor
public enum ImageSuccessCode implements BaseSuccessCode {

    CREATE_PRESIGNED_URL(HttpStatus.OK, "IMAGE200_1", "presigned URL을 성공적으로 발급했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}