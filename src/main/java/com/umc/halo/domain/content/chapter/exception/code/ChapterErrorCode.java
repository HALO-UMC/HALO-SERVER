package com.umc.halo.domain.content.chapter.exception.code;

import com.umc.halo.global.apiPayload.code.*;
import lombok.*;
import org.springframework.http.*;

@Getter
@RequiredArgsConstructor
public enum ChapterErrorCode implements BaseErrorCode {

    UNOPENED_CHAPTER(HttpStatus.FORBIDDEN, "CHAPTER403_1", "아직 열리지 않은 장입니다."),
    COMPLETED_CHAPTER(HttpStatus.FORBIDDEN, "CHAPTER403_2", "이미 완료한 장입니다."),
    NOT_FOUND_CHAPTER(HttpStatus.NOT_FOUND, "CHAPTER404_1", "존재하지 않는 장입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
