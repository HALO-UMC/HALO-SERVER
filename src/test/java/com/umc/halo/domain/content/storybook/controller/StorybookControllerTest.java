package com.umc.halo.domain.content.storybook.controller;

import com.umc.halo.domain.content.storybook.dto.StorybookResDTO;
import com.umc.halo.domain.content.storybook.enums.HomeStatus;
import com.umc.halo.domain.content.storybook.enums.StorybookStatus;
import com.umc.halo.domain.content.storybook.exception.StorybookException;
import com.umc.halo.domain.content.storybook.exception.code.StorybookErrorCode;
import com.umc.halo.domain.content.storybook.service.StorybookService;
import com.umc.halo.global.config.SecurityConfig;
import com.umc.halo.global.security.JwtAccessDeniedHandler;
import com.umc.halo.global.security.JwtAuthenticationEntryPoint;
import com.umc.halo.global.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * StorybookController의 요청/응답 매핑, 인증된 memberId 전달, 예외 -> 상태코드 변환을 검증.
 * SecurityConfigTest와 동일하게 실제 JwtAuthFilter를 태워 Authorization 헤더 기반 인증을 검증한다.
 * GeneralExceptionAdvice(@RestControllerAdvice)는 @WebMvcTest 슬라이스에서도 controllers 필터와 무관하게
 * 자동으로 포함되므로 별도 @Import 없이 예외 -> HTTP 상태코드 변환이 그대로 동작한다.
 */
@WebMvcTest(controllers = StorybookController.class)
@Import({SecurityConfig.class, JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class})
@EnableWebSecurity
class StorybookControllerTest {

    private static final String TOKEN = "valid-token";
    private static final Long MEMBER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StorybookService storybookService;
    @MockitoBean
    private JwtUtil jwtUtil;

    // 마지막 "토큰 없이 요청" 테스트에서는 JwtAuthFilter가 jwtUtil을 아예 호출하지 않으므로,
    // strict stubbing에 걸리지 않도록 lenient로 공통 인증 스텁을 준비한다.
    @BeforeEach
    void setUpAuth() {
        lenient().when(jwtUtil.isValid(TOKEN)).thenReturn(true);
        lenient().when(jwtUtil.isRefreshToken(TOKEN)).thenReturn(false);
        lenient().when(jwtUtil.getMemberId(TOKEN)).thenReturn(MEMBER_ID);
    }

    @Test
    void 홈_조회는_인증된_memberId로_서비스를_호출하고_결과를_반환한다() throws Exception {
        given(storybookService.getHome(MEMBER_ID)).willReturn(
                StorybookResDTO.GetHome.builder()
                        .homeStatus(HomeStatus.NO_STORYBOOK)
                        .memberName("혜담님")
                        .bookshelf(List.of())
                        .inProgressStorybooks(List.of())
                        .recommendedStorybooks(List.of())
                        .build()
        );

        mockMvc.perform(get("/api/v1/home").header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.homeStatus").value("NO_STORYBOOK"))
                .andExpect(jsonPath("$.result.memberName").value("혜담님"));
    }

    @Test
    void 스토리북_목록_조회는_인증된_memberId로_서비스를_호출한다() throws Exception {
        given(storybookService.getStorybookList(MEMBER_ID)).willReturn(
                StorybookResDTO.GetStorybookList.builder()
                        .storybooks(List.of())
                        .situationalRecommendations(List.of())
                        .build()
        );

        mockMvc.perform(get("/api/v1/storybooks").header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isOk());
    }

    @Test
    void 추천_스토리북_조회는_인증된_memberId로_서비스를_호출한다() throws Exception {
        given(storybookService.getRecommendedStorybooks(MEMBER_ID)).willReturn(
                StorybookResDTO.GetRecommendedStorybooks.builder()
                        .storybooks(List.of())
                        .build()
        );

        mockMvc.perform(get("/api/v1/storybooks/recommended").header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isOk());
    }

    @Test
    void 스토리북_상세_조회는_경로변수와_memberId를_그대로_서비스에_전달한다() throws Exception {
        given(storybookService.getStorybookDetail(5L, MEMBER_ID)).willReturn(
                StorybookResDTO.GetStorybookDetail.builder()
                        .storybookId(5L)
                        .title("스토리북5")
                        .description("설명")
                        .imageUrl("image.png")
                        .completedChapterCount(0)
                        .progressPercentage(0)
                        .chapters(List.of())
                        .build()
        );

        mockMvc.perform(get("/api/v1/storybooks/5").header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.storybookId").value(5));
    }

    @Test
    void 스토리북_상세_조회에서_존재하지_않는_스토리북이면_404를_반환한다() throws Exception {
        given(storybookService.getStorybookDetail(999L, MEMBER_ID))
                .willThrow(new StorybookException(StorybookErrorCode.NOT_FOUND));

        mockMvc.perform(get("/api/v1/storybooks/999").header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isNotFound());
    }

    @Test
    void 스토리북_시작은_201과_함께_생성된_리소스를_반환한다() throws Exception {
        given(storybookService.startStorybook(5L, MEMBER_ID)).willReturn(
                StorybookResDTO.StartStorybook.builder()
                        .memberStorybookId(10L)
                        .storybookId(5L)
                        .status(StorybookStatus.IN_PROGRESS)
                        .build()
        );

        mockMvc.perform(post("/api/v1/storybooks/5/member-storybooks").header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result.memberStorybookId").value(10))
                .andExpect(jsonPath("$.result.status").value("IN_PROGRESS"));
    }

    @Test
    void 스토리북_시작시_이미_진행중이면_409를_반환한다() throws Exception {
        given(storybookService.startStorybook(5L, MEMBER_ID))
                .willThrow(new StorybookException(StorybookErrorCode.ALREADY_IN_PROGRESS));

        mockMvc.perform(post("/api/v1/storybooks/5/member-storybooks").header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isConflict());
    }

    @Test
    void 토큰_없이_요청하면_401을_반환한다() throws Exception {
        mockMvc.perform(get("/api/v1/home"))
                .andExpect(status().isUnauthorized());
    }
}