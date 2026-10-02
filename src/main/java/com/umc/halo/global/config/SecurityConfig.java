package com.umc.halo.global.config;

import com.umc.halo.global.security.*;
import lombok.*;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.http.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.http.*;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.*;

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
            // 헬스체크 (배포 스크립트에서 인증 없이 확인)
            "/actuator/health",
            // 인증(소셜 로그인 / 토큰 재발급)
            "/api/v1/auth/login",
            "/api/v1/auth/reissue",
            // 스레드풀 메트릭 (local/dev만 노출, prod는 management 설정으로 차단)
            "/actuator/metrics",
            "/actuator/metrics/**",

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

    // JwtAuthFilter는 SecurityFilterChain 안(addFilterBefore)에서만 실행돼야 함.
    // @Component로 등록된 Filter 빈은 Spring Boot가 서블릿 컨테이너에도 별도로 자동 등록하는데,
    // 그러면 Security 체인 밖에서 한 번 더 실행되어(OncePerRequestFilter 특성상) 인증 결과가 무효화될 수 있음.
    // (운영에서는 우연히 순서상 문제가 안 드러났지만, @WebMvcTest 슬라이스에서는 401로 드러남)
    // -> 전역 자동 등록을 막고 Security 체인 안에서만 동작하도록 고정.
    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilterRegistration() {
        FilterRegistrationBean<JwtAuthFilter> registrationBean = new FilterRegistrationBean<>(jwtAuthFilter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }
}