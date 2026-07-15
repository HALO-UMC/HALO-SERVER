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

    ALREADY_COMPLETED_TODAY(HttpStatus.CONFLICT, "CHAPTER409_1", "오늘 이미 이 스토리북의 장을 완료했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}