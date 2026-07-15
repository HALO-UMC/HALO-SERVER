package com.umc.halo.domain.record.excption.code;

import com.umc.halo.global.apiPayload.code.*;
import lombok.*;
import org.springframework.http.*;

@Getter
@RequiredArgsConstructor
public enum RecordErrorCode implements BaseErrorCode {

    INCORRECT_COVER_TYPE(HttpStatus.BAD_REQUEST, "CHAPTER400_1", "선택한 커버 타입과 입력값이 일치하지 않습니다."),
    INCOMPLETE_ANSWERS(HttpStatus.BAD_REQUEST, "CHAPTER400_4", "모든 장 질문에 대한 답변이 필요합니다."),
    MISSING_COVER_TYPE(HttpStatus.BAD_REQUEST, "CHAPTER400_5", "커버 타입 선택이 필요합니다."),
    MISSING_EMOTION(HttpStatus.BAD_REQUEST, "CHAPTER400_6", "감정 선택이 필요합니다."),

    NOT_COMPLETED_MEMBER_CHAPTER(HttpStatus.FORBIDDEN, "CHAPTER403_4", "아직 완료되지 않은 장 기록입니다."),

    NOT_FOUND_MEMBER_CHAPTER(HttpStatus.NOT_FOUND, "CHAPTER404_4", "존재하지 않는 장 기록입니다."),

    ALREADY_COMPLETED_TODAY(HttpStatus.CONFLICT, "CHAPTER409_1", "오늘 이미 이 스토리북의 장을 완료하여 장 기록을 작성할 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}