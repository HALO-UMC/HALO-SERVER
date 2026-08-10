package com.umc.halo.global.security;

import com.umc.halo.global.apiPayload.code.GeneralErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.warn("인증되지 않은 요청. uri={}, message={}", request.getRequestURI(), authException.getMessage());
        response.setHeader("WWW-Authenticate", "Bearer realm=\"halo\"");
        SecurityResponseWriter.write(response, GeneralErrorCode.UNAUTHORIZED);
    }
}