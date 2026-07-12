package com.umc.halo.domain.content.storybook.controller;

import com.umc.halo.domain.content.storybook.dto.response.StorybookDetailResponse;
import com.umc.halo.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "스토리북", description = "스토리북 목록/상세/시작하기 관련 API")
public interface StorybookControllerDocs {

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
                                    {
                                      "isSuccess": true,
                                      "code": "STORYBOOK200_2",
                                      "message": "스토리북 상세를 성공적으로 조회했습니다.",
                                      "result": {
                                        "storybookId": 1,
                                        "title": "오래전 당신",
                                        "description": "부모님을 '부모'가 아닌...",
                                        "imageUrl": "https://cdn.halo.com/storybook/1.png",
                                        "chapters": []
                                      }
                                    }
                                    """)
                    )
            )
    })
    ApiResponse<StorybookDetailResponse.GetStorybookDetail> getStorybookDetail(
            @Parameter(description = "스토리북 ID", required = true) Long storybookId,
            @AuthenticationPrincipal Long memberId
    );
}