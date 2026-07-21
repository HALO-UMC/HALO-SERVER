package com.umc.halo.domain.image.exception.code;

import com.umc.halo.global.apiPayload.code.*;
import lombok.*;
import org.springframework.http.*;

@Getter
@RequiredArgsConstructor
public enum ImageErrorCode implements BaseErrorCode {

    INVALID_CONTENT_TYPE(HttpStatus.BAD_REQUEST, "IMAGE400_1", "지원하지 않는 파일 형식입니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "IMAGE400_2", "이미지 용량이 제한을 초과했습니다.(최대 10MB)"),
    FORBIDDEN_IMAGE_KEY(HttpStatus.FORBIDDEN, "IMAGE403_1", "본인이 업로드한 이미지가 아닙니다."),
    NOT_FOUND_IN_S3(HttpStatus.NOT_FOUND, "IMAGE404_1", "S3에 해당 이미지가 존재하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}