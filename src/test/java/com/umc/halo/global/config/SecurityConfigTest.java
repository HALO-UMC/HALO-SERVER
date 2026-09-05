package com.umc.halo.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.halo.domain.member.controller.MemberController;
import com.umc.halo.domain.member.dto.MemberReqDTO;
import com.umc.halo.domain.member.dto.MemberResDTO;
import com.umc.halo.domain.member.service.MemberService;
import com.umc.halo.domain.term.controller.TermController;
import com.umc.halo.domain.term.service.TermService;
import com.umc.halo.global.security.JwtAccessDeniedHandler;
import com.umc.halo.global.security.JwtAuthenticationEntryPoint;
import com.umc.halo.global.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * SecurityConfig의 경로별 인증 규칙(PUBLIC_URIS, GET /api/v1/terms만 Public, 나머지 Private)이
 * 실제 필터 체인(JwtAuthFilter 포함)에서 의도대로 동작하는지 검증.
 * 웹 계층만 띄우는 슬라이스 테스트라 DataSource/Flyway/Firebase/S3는 로딩되지 않는다.
 * JwtAuthFilter는 @WebMvcTest가 Filter 타입 빈으로 자동 스캔하므로 별도 @Import 불필요.
 */
@WebMvcTest(controllers = {MemberController.class, TermController.class})
@Import({SecurityConfig.class, JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class})
@EnableWebSecurity
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private MemberService memberService;
    @MockitoBean
    private TermService termService;
    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void 로그인은_토큰_없이도_통과한다() throws Exception {
        given(memberService.login(any())).willReturn(
                MemberResDTO.Login.builder()
                        .accessToken("a").refreshToken("r")
                        .isNewUser(false).onboardingCompleted(true).termsAgreed(true)
                        .build()
        );

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MemberReqDTO.Login("KAKAO", "token"))))
                .andExpect(status().isOk());
    }

    @Test
    void 토큰_재발급은_토큰_없이도_통과한다() throws Exception {
        given(memberService.tokenReissue(any())).willReturn(
                MemberResDTO.TokenReissue.builder()
                        .accessToken("a").refreshToken("r")
                        .onboardingCompleted(true).termsAgreed(true)
                        .build()
        );

        mockMvc.perform(post("/api/v1/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MemberReqDTO.TokenReissue("refresh-token"))))
                .andExpect(status().isOk());
    }

    @Test
    void 약관_목록_조회는_GET만_토큰_없이도_통과한다() throws Exception {
        given(termService.getTerms()).willReturn(List.of());

        mockMvc.perform(get("/api/v1/terms"))
                .andExpect(status().isOk());
    }

    @Test
    void 약관_목록_POST_요청은_토큰_없으면_401이다() throws Exception {
        mockMvc.perform(post("/api/v1/terms"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 약관_동의는_POST라서_토큰_없으면_401이다() throws Exception {
        mockMvc.perform(post("/api/v1/terms/agreements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 로그아웃은_Private이라_토큰_없으면_401이다() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 내정보_조회는_Private이라_토큰_없으면_401이다() throws Exception {
        mockMvc.perform(get("/api/v1/members/me"))
                .andExpect(status().isUnauthorized());
    }
}