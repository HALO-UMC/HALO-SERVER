package com.umc.halo.domain.record.excption.code;

import com.umc.halo.global.apiPayload.code.*;
import lombok.*;
import org.springframework.http.*;

@RequiredArgsConstructor
@Getter
public enum RecordSuccessCode implements BaseSuccessCode {

    WRITE_CHAPTER_RECORD(HttpStatus.OK, "CHAPTER200_2", "장을 성공적으로 작성했습니다."),
    READ_CHAPTER_RECORD(HttpStatus.OK, "CHAPTER200_3", "완료된 장을 성공적으로 조회했습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
