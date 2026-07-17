package com.umc.halo.global.apiPayload.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GeneralErrorCode implements BaseErrorCode {

	// 400 Bad Request (잘못된 요청)
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400_1", "잘못된 요청입니다."),
	INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "COMMON400_2", "잘못된 파라미터가 입력되었습니다."),
	MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "COMMON400_3", "필수 파라미터가 누락되었습니다."),

	// 404 Not Found (리소스 없음)
	NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404_1", "요청하신 리소스를 찾을 수 없습니다."),

	// 401 Unauthorized (인증 실패)
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH401_1", "토큰이 만료되었습니다."),

	// 403 Forbidden (권한 없음)
	FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403_1", "접근 권한이 없습니다."),

	// 405 Method Not Allowed (지원하지 않는 HTTP 메소드)
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON405_1", "지원하지 않는 HTTP 메소드입니다."),

	// 409 Conflict (중복/충돌)
	CONFLICT(HttpStatus.CONFLICT, "COMMON409_1", "이미 존재하는 데이터이거나 요청이 충돌합니다."),

	// 415 Unsupported Media Type (지원하지 않는 Content-Type)
	UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "COMMON415_1", "지원하지 않는 Content-Type입니다."),

	// 500 Internal Server Error (서버 내부 에러)
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500_1", "서버 내부 오류가 발생했습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
