package com.umc.halo.domain.content.storybook.exception.code;

import com.umc.halo.global.apiPayload.code.*;
import lombok.*;
import org.springframework.http.*;

@Getter
@RequiredArgsConstructor
public enum StorybookErrorCode implements BaseErrorCode {
    NOT_FOUND_CHARACTER(HttpStatus.NOT_FOUND, "STORYBOOK404_2", "존재하지 않는 캐릭터입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
