package com.umc.halo.global.config;

import com.umc.halo.global.security.JwtAccessDeniedHandler;
import com.umc.halo.global.security.JwtAuthFilter;
import com.umc.halo.global.security.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    // 인증 없이 접근 가능한 경로(Public)
    private static final String[] PUBLIC_URIS = {
            // Swagger
            "/swagger-ui/**",
            "/v3/api-docs/**",
            // 인증(소셜 로그인 / 토큰 재발급)
            "/api/v1/auth/login",
            "/api/v1/auth/reissue",

    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 토큰(JWT) 기반이라 세션 안 씀
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 폼 로그인/기본 인증 안 씀 (소셜 로그인 전용)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                // 토큰 기반이라 csrf 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                // 경로별 접근 제어
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URIS).permitAll()
                        // 약관 목록 조회(GET /api/v1/terms)는 Public
                        .requestMatchers(HttpMethod.GET, "/api/v1/terms").permitAll()
                        // 그 외 전부 인증 필요(Private)
                        .anyRequest().authenticated()
                )

        // 인증/인가 실패 시 공통 응답 포맷으로 반환
                .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
        )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}