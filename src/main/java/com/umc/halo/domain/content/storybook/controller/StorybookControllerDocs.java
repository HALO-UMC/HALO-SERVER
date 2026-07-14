package com.umc.halo.domain.content.storybook.controller;

import com.umc.halo.domain.content.storybook.dto.response.StorybookDetailResponse;
import com.umc.halo.domain.content.storybook.dto.response.StorybookListResponse;
import com.umc.halo.domain.content.storybook.dto.response.StorybookRecommendResponse;
import com.umc.halo.domain.content.storybook.dto.response.StorybookStartResponse;
import com.umc.halo.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "스토리북", description = "스토리북 목록/상세/시작하기/추천 관련 API")
public interface StorybookControllerDocs {

    @Operation(
            summary = "스토리북 목록 조회 By 한혜담",
            description = "사용자의 스토리북 목록과 진행 상태, 상황별 추천 5개 카테고리를 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                                    { ... STORYBOOK200_1 example ... }
                                    """)
                    )
            )
    })
    ApiResponse<StorybookListResponse.GetStorybookList> getStorybookList(
            @AuthenticationPrincipal Long memberId
    );

    @Operation(
            summary = "스토리북 상세 조회 By 한혜담",
            description = "스토리북 상세 정보와 10개 장 목록(진행 상태 포함)을 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                                    { ... STORYBOOK200_2 example ... }
                                    """)
                    )
            )
    })
    ApiResponse<StorybookDetailResponse.GetStorybookDetail> getStorybookDetail(
            @Parameter(description = "스토리북 ID", required = true) Long storybookId,
            @AuthenticationPrincipal Long memberId
    );

    @Operation(
            summary = "스토리북 시작하기 By 한혜담",
            description = "선택한 스토리북을 시작합니다. 이미 진행중이면 409(STORYBOOK409_1), 이미 완료했으면 409(STORYBOOK409_2)를 반환합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                                    { ... STORYBOOK200_3 example ... }
                                    """)
                    )
            )
    })
    ApiResponse<StorybookStartResponse.StartStorybook> startStorybook(
            @Parameter(description = "스토리북 ID", required = true) Long storybookId,
            @AuthenticationPrincipal Long memberId
    );

    @Operation(
            summary = "추천 스토리북 조회 By 한혜담",
            description = "온보딩 시 선택한 목적 태그를 기반으로 추천 스토리북 2개를 조회합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                                    { ... STORYBOOK200_4 example ... }
                                    """)
                    )
            )
    })
    ApiResponse<StorybookRecommendResponse.GetRecommendedStorybooks> getRecommendedStorybooks(
            @AuthenticationPrincipal Long memberId
    );
}