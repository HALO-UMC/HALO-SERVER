package com.umc.halo.domain.relationship.exception.code;

import com.umc.halo.global.apiPayload.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RelationshipSuccessCode implements BaseSuccessCode {
    INFO_SUCCESS(HttpStatus.OK,
            "RELATIONSHIP200_1",
            "관계 정보 조회 성공")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}