package com.umc.halo.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.halo.global.apiPayload.ApiResponse;
import com.umc.halo.global.apiPayload.code.GeneralErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// 인증/인가 실패 응답을 공통 포맷(ApiResponse)으로 작성
public final class SecurityResponseWriter {

    // Spring 빈 주입 대신 로컬 static 인스턴스 사용
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private SecurityResponseWriter() {}

    public static void write(HttpServletResponse response, GeneralErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ApiResponse<Void> body = ApiResponse.onFailure(errorCode, null);
        response.getWriter().write(OBJECT_MAPPER.writeValueAsString(body));
    }
}