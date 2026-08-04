package com.umc.halo;

import com.umc.halo.global.security.JwtUtil;

public class TokenGenerator {
    public static void main(String[] args) {
        JwtUtil jwtUtil = new JwtUtil("local-test-secret-key-1234567890abcdefg", 3600000L, 604800000L);
        System.out.println(jwtUtil.createAccessToken(1L));
    }
}