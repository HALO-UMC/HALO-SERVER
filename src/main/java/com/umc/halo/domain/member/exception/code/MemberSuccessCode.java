package com.umc.halo.domain.member.exception.code;

import com.umc.halo.global.apiPayload.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberSuccessCode implements BaseSuccessCode {

    INFO_SUCCESS(HttpStatus.OK,
            "MEMBER200_1",
            "내 정보 조회 성공"),
    DELETE_SUCCESS(HttpStatus.OK,
            "MEMBER200_2",
            "회원 탈퇴가 완료되었습니다."),
    MEMBER_ACCESS_UPDATED_SUCCESS(HttpStatus.OK,
            "MEMBER200_3",
            "접속 시간 갱신 성공"
    );
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
