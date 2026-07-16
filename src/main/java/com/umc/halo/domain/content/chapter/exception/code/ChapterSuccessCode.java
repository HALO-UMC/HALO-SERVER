package com.umc.halo.domain.content.chapter.exception.code;

import com.umc.halo.global.apiPayload.code.*;
import lombok.*;
import org.springframework.http.*;

@Getter
@RequiredArgsConstructor
public enum ChapterSuccessCode implements BaseSuccessCode {

    // 오늘의 장
    TODAY_CHAPTER(HttpStatus.OK, "CHAPTER200_1", "오늘의 장을 성공적으로 조회했습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
