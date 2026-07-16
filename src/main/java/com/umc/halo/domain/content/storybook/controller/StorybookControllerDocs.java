package com.umc.halo.domain.content.storybook.controller;

import com.umc.halo.domain.content.storybook.apiPayload.HomeSuccessCode;
import com.umc.halo.domain.content.storybook.dto.response.HomeResponse;
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

@Tag(name = "스토리북", description = "홈/스토리북 목록/상세/시작하기/추천 관련 API")
public interface StorybookControllerDocs {

    @Operation(
            summary = "홈 화면 조회",
            description = "사용자의 진행 중인 스토리북 현황, 책장 목록, 추천 스토리북과 홈 화면 상태를 조회합니다."
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
                                      "code": "HOME200_1",
                                      "message": "홈 화면을 성공적으로 조회했습니다.",
                                      "result": {
                                        "homeStatus": "MULTIPLE_IN_PROGRESS",
                                        "memberName": "김하로님",
                                        "representativeStorybook": {
                                          "storybookId": 3,
                                          "title": "가족의 온도",
                                          "currentChapterTitle": "나와 같은 나이였던 시절",
                                          "currentChapterOrder": 1,
                                          "todayAvailable": true
                                        },
                                        "otherInProgressCount": 1,
                                        "bookshelf": [
                                          {
                                            "storybookId": 1,
                                            "title": "오래 전 당신",
                                            "themeOrder": 1,
                                            "spineColor": "#E4B7A0",
                                            "status": "COMPLETED"
                                          },
                                          {
                                            "storybookId": 3,
                                            "title": "가족의 온도",
                                            "themeOrder": 3,
                                            "spineColor": "#B7E4A0",
                                            "status": "IN_PROGRESS"
                                          }
                                        ],
                                        "recommendedStorybooks": []
                                      }
                                    }
                                    """)
                    )
            )
    })
    ApiResponse<HomeResponse.GetHome> getHome(
            @AuthenticationPrincipal Long memberId
    );

    @Operation(
            summary = "스토리북 목록 조회",
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
                                    {
                                      "isSuccess": true,
                                      "code": "STORYBOOK200_1",
                                      "message": "스토리북 목록을 성공적으로 조회했습니다.",
                                      "result": {
                                        "storybooks": [
                                          {
                                            "storybookId": 1,
                                            "title": "오래 전 당신",
                                            "themeOrder": 1,
                                            "shortDescription": "가족과의 만남",
                                            "imageUrl": "https://example.com/storybook1.png",
                                            "status": "COMPLETED",
                                            "lastChapterOrder": 10,
                                            "lastCompletedDate": "2026-07-14"
                                          },
                                          {
                                            "storybookId": 2,
                                            "title": "당신 사용설명서",
                                            "themeOrder": 2,
                                            "shortDescription": "나를 아는 시간",
                                            "imageUrl": "https://example.com/storybook2.png",
                                            "status": "NOT_STARTED",
                                            "lastChapterOrder": null,
                                            "lastCompletedDate": null
                                          }
                                        ],
                                        "situationalRecommendations": [
                                          {
                                            "tag": "어색하지 않게 이야기하고 싶어요",
                                            "storybooks": [
                                              {
                                                "storybookId": 1,
                                                "title": "오래 전 당신",
                                                "imageUrl": "https://example.com/storybook1.png",
                                                "recommendationReasonText": "어색하지 않게 이야기하고 싶어요"
                                              }
                                            ]
                                          }
                                        ]
                                      }
                                    }
                                    """)
                    )
            )
    })
    ApiResponse<StorybookListResponse.GetStorybookList> getStorybookList(
            @AuthenticationPrincipal Long memberId
    );

    @Operation(
            summary = "스토리북 상세 조회",
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
                                        "storybookId": 4,
                                        "title": "취향이 닿는 날",
                                        "description": "좋아하는 것과 싫어하는 것을 하나씩 나누며, 서로의 취향이 닿는 순간들을 발견하는 이야기입니다.",
                                        "imageUrl": "https://example.com/storybook4.png",
                                        "chapters": [
                                          {
                                            "chapterOrder": 1,
                                            "title": "나와 같은 나이였던 시절",
                                            "imageUrl": "https://example.com/chapter1.png",
                                            "shortDescription": "부모님이 지금의 내 나이였을 때의 하루",
                                            "description": "부모님을 한 사람으로 바라보는 첫 장입니다.",
                                            "status": "COMPLETED"
                                          },
                                          {
                                            "chapterOrder": 2,
                                            "title": "소년과 소녀의 꿈",
                                            "imageUrl": "https://example.com/chapter2.png",
                                            "shortDescription": "목차 예시2",
                                            "description": "부모님에게도 작고 선명했던 어린 시절의 꿈이 있었습니다.",
                                            "status": "LOCKED"
                                          }
                                        ]
                                      }
                                    }
                                    """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 스토리북",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "STORYBOOK404_1",
                                      "message": "존재하지 않는 스토리북입니다.",
                                      "result": null
                                    }
                                    """)
                    )
            )
    })
    ApiResponse<StorybookDetailResponse.GetStorybookDetail> getStorybookDetail(
            @Parameter(description = "스토리북 ID", required = true) Long storybookId,
            @AuthenticationPrincipal Long memberId
    );

    @Operation(
            summary = "스토리북 시작하기",
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
                                    {
                                      "isSuccess": true,
                                      "code": "STORYBOOK200_3",
                                      "message": "스토리북을 시작했습니다.",
                                      "result": {
                                        "memberStorybookId": 4,
                                        "storybookId": 2,
                                        "status": "IN_PROGRESS"
                                      }
                                    }
                                    """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이미 진행중이거나 이미 완료한 스토리북",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "STORYBOOK409_1",
                                      "message": "이미 시작한 스토리북입니다.",
                                      "result": null
                                    }
                                    """)
                    )
            )
    })
    ApiResponse<StorybookStartResponse.StartStorybook> startStorybook(
            @Parameter(description = "스토리북 ID", required = true) Long storybookId,
            @AuthenticationPrincipal Long memberId
    );

    @Operation(
            summary = "추천 스토리북 조회",
            description = "온보딩 시 선택한 목적 태그를 기반으로 추천 스토리북 2 개를 조회합니다."
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
                                      "code": "STORYBOOK200_4",
                                      "message": "추천 스토리북을 성공적으로 조회했습니다.",
                                      "result": {
                                        "storybooks": [
                                          {
                                            "storybookId": 1,
                                            "title": "오래 전 당신",
                                            "shortDescription": "가족과의 만남",
                                            "imageUrl": "https://example.com/storybook1.png",
                                            "recommendationReasonText": "어색하지 않게 이야기하고 싶어요"
                                          },
                                          {
                                            "storybookId": 2,
                                            "title": "당신 사용설명서",
                                            "shortDescription": "나를 아는 시간",
                                            "imageUrl": "https://example.com/storybook2.png",
                                            "recommendationReasonText": "부모님을 더 알고 싶어요"
                                          }
                                        ]
                                      }
                                    }
                                    """)
                    )
            )
    })
    ApiResponse<StorybookRecommendResponse.GetRecommendedStorybooks> getRecommendedStorybooks(
            @AuthenticationPrincipal Long memberId
    );
}
