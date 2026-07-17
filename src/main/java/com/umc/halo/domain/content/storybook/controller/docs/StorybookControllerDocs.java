package com.umc.halo.domain.content.storybook.controller.docs;

import com.umc.halo.domain.content.storybook.dto.StorybookResDTO;
import com.umc.halo.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "스토리북 API")
public interface StorybookControllerDocs {


    @Operation(
            summary = "홈 화면 조회 API",
            description = """
                    ### 홈 화면 조회

                    로그인한 회원의 진행 중인 스토리북 현황, 책장 목록, 추천 스토리북과 홈 화면 상태를 조회합니다.

                    **요청 형식**
                    - Header
                        - Authorization: Bearer {accessToken}

                    **동작 방식**
                    1. 인증된 회원의 진행 중인 스토리북들을 조회합니다.
                    2. 대표 스토리북(가장 최근에 진행 중인 것)의 진행 정보를 계산합니다.
                    3. 전체 스토리북의 책장 상태(NOT_STARTED/IN_PROGRESS/COMPLETED)를 계산합니다.
                    4. 진행 중인 스토리북이 하나도 없으면 추천 스토리북을 함께 조회합니다.
                    5. 계산된 홈 화면 상태(homeStatus)와 함께 결과를 반환합니다.
                    """
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
    ApiResponse<StorybookResDTO.GetHome> getHome(
            @AuthenticationPrincipal Long memberId
    );

    @Operation(
            summary = "스토리북 목록 조회 API",
            description = """
                    ### 스토리북 목록 조회

                    로그인한 회원의 스토리북 목록과 진행 상태, 상황별 추천 5개 카테고리를 조회합니다.

                    **요청 형식**
                    - Header
                        - Authorization: Bearer {accessToken}

                    **동작 방식**
                    1. 전체 스토리북을 테마 순서대로 조회합니다.
                    2. 회원별 진행 상태(NOT_STARTED/IN_PROGRESS/TODAY_DONE/COMPLETED)를 계산합니다.
                    3. 회원이 온보딩에서 선택한 목적 태그를 기준으로 상황별 추천 카테고리를 구성합니다.
                    4. 스토리북 목록과 상황별 추천 결과를 함께 반환합니다.
                    """
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
    ApiResponse<StorybookResDTO.GetStorybookList> getStorybookList(
            @AuthenticationPrincipal Long memberId
    );

    @Operation(
            summary = "스토리북 상세 조회 API",
            description = """
                    ### 스토리북 상세 조회

                    스토리북 상세 정보와 10개 장 목록(진행 상태 포함)을 조회합니다.

                    **요청 형식**
                    - Header
                        - Authorization: Bearer {accessToken}
                    - Path Variable
                        - storybookId: 조회할 스토리북 ID

                    **동작 방식**
                    1. storybookId로 스토리북을 조회합니다. 존재하지 않으면 404(STORYBOOK404_1)를 반환합니다.
                    2. 회원의 장별 완료 여부를 조회합니다.
                    3. member_storybook의 lastCompletedDate로 오늘 완료 여부를 확인합니다.
                    4. 각 장의 상태(COMPLETED/TODAY/TODAY_LOCKED/LOCKED)를 계산하여 반환합니다.
                    """
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
    ApiResponse<StorybookResDTO.GetStorybookDetail> getStorybookDetail(
            @Parameter(description = "스토리북 ID", required = true) Long storybookId,
            @AuthenticationPrincipal Long memberId
    );

    @Operation(
            summary = "스토리북 시작하기 API",
            description = """
                    ### 스토리북 시작하기

                    선택한 스토리북을 시작합니다.

                    **요청 형식**
                    - Header
                        - Authorization: Bearer {accessToken}
                    - Path Variable
                        - storybookId: 시작할 스토리북 ID

                    **동작 방식**
                    1. storybookId로 스토리북을 조회합니다. 존재하지 않으면 404(STORYBOOK404_1)를 반환합니다.
                    2. 이미 시작한 스토리북인지 확인합니다.
                    3. 완료까지 한 경우 409(STORYBOOK409_2), 진행 중인 경우 409(STORYBOOK409_1)를 반환합니다.
                    4. 처음 시작하는 경우 member_storybook을 생성하고 1번째 장부터 진행하도록 설정합니다.
                    5. 생성된 진행 정보를 반환합니다.
                    """
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
    ApiResponse<StorybookResDTO.StartStorybook> startStorybook(
            @Parameter(description = "스토리북 ID", required = true) Long storybookId,
            @AuthenticationPrincipal Long memberId
    );

    @Operation(
            summary = "추천 스토리북 조회 API",
            description = """
                    ### 추천 스토리북 조회

                    온보딩 시 선택한 목적 태그를 기반으로 추천 스토리북 2개를 조회합니다.

                    **요청 형식**
                    - Header
                        - Authorization: Bearer {accessToken}

                    **동작 방식**
                    1. 회원이 온보딩에서 선택한 목적 태그를 조회합니다.
                    2. 태그와 매칭되는 스토리북을 우선순위(PRIMARY > SECONDARY) 순으로 정렬합니다.
                    3. 매칭된 스토리북이 2개 미만이면 테마 순서를 기준으로 나머지를 채웁니다.
                    4. 최종 추천 스토리북 2개를 반환합니다.
                    """
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
    ApiResponse<StorybookResDTO.GetRecommendedStorybooks> getRecommendedStorybooks(
            @AuthenticationPrincipal Long memberId
    );
}
