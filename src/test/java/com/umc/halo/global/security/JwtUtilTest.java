package com.umc.halo.global.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JwtUtil의 토큰 생성/검증 로직 자체를 검증한다.
 * accessToken/refreshToken 생성 후 memberId 왕복이 정확한지,
 * 만료·서명오류·형식오류 토큰에 대해 isValid/isExpired가 올바르게 판별하는지 확인.
 */
class JwtUtilTest {

    private static final String SECRET = "test-secret-key-for-jwt-util-test-1234567890";

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        // access 1시간, refresh 14일
        jwtUtil = new JwtUtil(SECRET, 1000L * 60 * 60, 1000L * 60 * 60 * 24 * 14);
    }

    @Test
    void accessToken_생성후_memberId를_정확히_추출한다() {
        String token = jwtUtil.createAccessToken(42L);

        assertThat(jwtUtil.getMemberId(token)).isEqualTo(42L);
        assertThat(jwtUtil.isValid(token)).isTrue();
        assertThat(jwtUtil.isRefreshToken(token)).isFalse();
    }

    @Test
    void refreshToken_생성후_memberId와_타입이_정확하다() {
        String token = jwtUtil.createRefreshToken(7L);

        assertThat(jwtUtil.getMemberId(token)).isEqualTo(7L);
        assertThat(jwtUtil.isValid(token)).isTrue();
        assertThat(jwtUtil.isRefreshToken(token)).isTrue();
    }

    @Test
    void 만료된_토큰은_isValid가_false이고_isExpired가_true다() {
        JwtUtil expiredJwtUtil = new JwtUtil(SECRET, -1000L, 1000L * 60);
        String token = expiredJwtUtil.createAccessToken(1L);

        assertThat(expiredJwtUtil.isValid(token)).isFalse();
        assertThat(expiredJwtUtil.isExpired(token)).isTrue();
    }

    @Test
    void 서명이_다른_토큰은_isValid가_false이고_isExpired는_false다() {
        JwtUtil otherJwtUtil = new JwtUtil("different-secret-key-should-fail-verify-000", 1000L * 60 * 60, 1000L * 60 * 60);
        String token = otherJwtUtil.createAccessToken(1L);

        assertThat(jwtUtil.isValid(token)).isFalse();
        assertThat(jwtUtil.isExpired(token)).isFalse();
    }

    @Test
    void 형식이_잘못된_토큰_문자열은_isValid가_false다() {
        assertThat(jwtUtil.isValid("not-a-jwt-token")).isFalse();
    }
}