package com.umc.halo.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * JwtAuthFilter는 토큰이 없거나 유효하지 않아도 에러 응답을 만들지 않고
 * 인증 세팅 없이 다음 필터로 넘긴다 (Public/Private 판단은 SecurityConfig가 담당).
 * 유효한 accessToken일 때만 SecurityContext에 memberId로 인증 객체를 저장하는지 검증.
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void Authorization_헤더가_없으면_인증없이_다음_필터로_넘긴다() throws Exception {
        given(request.getHeader("Authorization")).willReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void Bearer_형식이_아니면_인증없이_다음_필터로_넘긴다() throws Exception {
        given(request.getHeader("Authorization")).willReturn("Basic abcdef");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void 유효하지_않은_토큰이면_인증없이_다음_필터로_넘긴다() throws Exception {
        given(request.getHeader("Authorization")).willReturn("Bearer invalid-token");
        given(jwtUtil.isValid("invalid-token")).willReturn(false);
        given(jwtUtil.isExpired("invalid-token")).willReturn(false);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void 만료된_토큰이면_인증없이_다음_필터로_넘긴다() throws Exception {
        given(request.getHeader("Authorization")).willReturn("Bearer expired-token");
        given(jwtUtil.isValid("expired-token")).willReturn(false);
        given(jwtUtil.isExpired("expired-token")).willReturn(true);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void accessToken_자리에_refreshToken을_쓰면_인증없이_다음_필터로_넘긴다() throws Exception {
        given(request.getHeader("Authorization")).willReturn("Bearer refresh-token");
        given(jwtUtil.isValid("refresh-token")).willReturn(true);
        given(jwtUtil.isRefreshToken("refresh-token")).willReturn(true);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void 유효한_accessToken이면_SecurityContext에_memberId로_인증객체를_저장한다() throws Exception {
        given(request.getHeader("Authorization")).willReturn("Bearer valid-token");
        given(jwtUtil.isValid("valid-token")).willReturn(true);
        given(jwtUtil.isRefreshToken("valid-token")).willReturn(false);
        given(jwtUtil.getMemberId("valid-token")).willReturn(99L);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isEqualTo(99L);
        verify(filterChain).doFilter(request, response);
    }
}