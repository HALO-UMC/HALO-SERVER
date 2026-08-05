package com.umc.halo.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // 토큰이 없거나 Bearer 형식이 아니면: 인증 세팅 없이 다음 필터로 넘김
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7); // "Bearer " 제거

        // 유효한 토큰이면 인증 객체를 만들어 SecurityContext에 저장
        if (jwtUtil.isValid(token) && !jwtUtil.isRefreshToken(token)) {
            Long memberId = jwtUtil.getMemberId(token);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            memberId,
                            null,
                            Collections.emptyList() // 권한 목록 (role 구분 없어 현재 비움)
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // 토큰이 유효하지 않아도 여기선 에러 응답 안 만듦 → Private면 뒤에서 차단됨

        filterChain.doFilter(request, response);
    }
}