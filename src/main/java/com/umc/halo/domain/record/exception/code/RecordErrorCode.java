package com.umc.halo.domain.record.exception.code;

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
    DUPLICATE_ANSWER(HttpStatus.BAD_REQUEST, "CHAPTER400_7", "같은 질문에 대한 답변이 중복되었습니다."),

    NOT_COMPLETED_MEMBER_CHAPTER(HttpStatus.FORBIDDEN, "CHAPTER403_4", "아직 완료되지 않은 장 기록입니다."),

    NOT_FOUND_MEMBER_CHAPTER(HttpStatus.NOT_FOUND, "CHAPTER404_4", "존재하지 않는 장 기록입니다."),

    ALREADY_COMPLETED_TODAY(HttpStatus.CONFLICT, "CHAPTER409_1", "오늘 이미 이 스토리북의 장을 완료하여 장 기록을 작성할 수 없습니다."),
    DUPLICATE_MEMBER_CHAPTER(HttpStatus.CONFLICT, "CHAPTER409_2", "동시에 처리된 요청으로 인한 충돌입니다. 다시 시도해주세요."),
    DUPLICATE_IMAGE_KEY(HttpStatus.CONFLICT, "CHAPTER409_3", "이미 다른 기록에서 사용 중인 이미지입니다. 다시 업로드해주세요.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}