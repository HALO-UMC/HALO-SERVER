package com.umc.halo.domain.content.chapter.exception.code;

import com.umc.halo.global.apiPayload.code.*;
import lombok.*;
import org.springframework.http.*;

@Getter
@RequiredArgsConstructor
public enum ChapterErrorCode implements BaseErrorCode {

    UNMATCHED_SCENE_CARD(HttpStatus.BAD_REQUEST, "CHAPTER400_2", "해당 장의 장면 카드가 아닙니다."),
    UNMATCHED_CHAPTER_QUESTION(HttpStatus.BAD_REQUEST, "CHAPTER400_3", "해당 장의 질문이 아닙니다."),

    UNOPENED_CHAPTER(HttpStatus.FORBIDDEN, "CHAPTER403_1", "아직 열리지 않은 장입니다."),
    COMPLETED_CHAPTER(HttpStatus.FORBIDDEN, "CHAPTER403_2", "이미 완료한 장입니다."),
    ALREADY_RECEIVED_TODAY(HttpStatus.FORBIDDEN, "CHAPTER403_3", "오늘 이미 장을 조회하였습니다."),

    NOT_FOUND_CHAPTER(HttpStatus.NOT_FOUND, "CHAPTER404_1", "존재하지 않는 장입니다."),
    NOT_FOUND_CHAPTER_QUESTION(HttpStatus.NOT_FOUND, "CHAPTER404_2", "존재하지 않는 장 질문입니다."),
    NOT_FOUND_SCENE_CARD(HttpStatus.NOT_FOUND, "CHAPTER404_3", "존재하지 않는 장면 카드입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
