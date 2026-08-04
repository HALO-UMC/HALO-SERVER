package com.umc.halo.domain.content.storybook.controller.docs;

import com.umc.halo.domain.content.storybook.dto.*;
import com.umc.halo.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.*;
import org.springframework.security.core.annotation.*;

@Tag(name = "스토리북 API")
public interface StorybookControllerDocs {


    @Operation(
            summary = "홈 화면 조회 API",
            description = """
                    # 홈 화면 조회
                    
                    로그인한 회원의 진행 중인 스토리북 현황, 책장 목록, 추천 스토리북과 홈 화면 상태를 조회합니다.
                    
                    ## 요청 형식
                    - **Header**
                        - Authorization: Bearer {accessToken}
                    
                    ## 동작 방식
                    1. 인증된 회원의 진행 중인 스토리북들을 조회합니다.
                    2. 진행 중인 스토리북 전체의 제목/진행 상황을 리스트로 계산합니다.
                    3. 진행 중인 스토리북이 하나도 없으면 추천 스토리북을 함께 조회합니다.
                    4. 계산된 홈 화면 상태(homeStatus)와 함께 결과를 반환합니다.
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "진행중인 스토리북이 있는 경우",
                                            value = """
                                        {
                                          "isSuccess": true,
                                          "code": "HOME200_1",
                                          "message": "홈 화면을 성공적으로 조회했습니다.",
                                          "result": {
                                            "homeStatus": "MULTIPLE_IN_PROGRESS",
                                            "memberName": "김하로님",
                                            "inProgressStorybooks": [
                                              {
                                                "storybookId": 3,
                                                "title": "가족의 온도",
                                                "imageUrl": "https://example.com/storybook3.png",
                                                "currentChapterOrder": 1,
                                                "totalChapterCount": 10,
                                                "todayAvailable": true
                                              },
                                              {
                                                "storybookId": 4,
                                                "title": "취향이 닿는 날",
                                                "imageUrl": "https://example.com/storybook4.png",
                                                "currentChapterOrder": 3,
                                                "totalChapterCount": 10,
                                                "todayAvailable": true
                                              }
                                            ],
                                            "recommendedStorybooks": []
                                          }
                                        }
                                        """
                                    ),
                                    @ExampleObject(
                                            name = "진행중인 스토리북이 없는 경우(추천)",
                                            value = """
                                        {
                                          "isSuccess": true,
                                          "code": "HOME200_1",
                                          "message": "홈 화면을 성공적으로 조회했습니다.",
                                          "result": {
                                            "homeStatus": "NO_STORYBOOK",
                                            "memberName": "김하로님",
                                            "inProgressStorybooks": [],
                                            "recommendedStorybooks": [
                                              {
                                                "storybookId": 1,
                                                "title": "오래 전 당신",
                                                "shortDescription": "가족과의 만남",
                                                "imageUrl": "https://example.com/storybook1.png",
                                                "recommendationReasonText": "부모님의 지난 시간을 알고 싶다면"
                                              },
                                              {
                                                "storybookId": 2,
                                                "title": "당신 사용설명서",
                                                "shortDescription": "나를 아는 시간",
                                                "imageUrl": "https://example.com/storybook2.png",
                                                "recommendationReasonText": "부모님을 더 잘 이해하고 싶다면"
                                              }
                                            ]
                                          }
                                        }
                                        """
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "accessToken 만료·유효하지 않음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "AUTH401_1",
                                      "message": "토큰이 만료되었습니다.",
                                      "result": null
                                    }
                                    """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 회원",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "MEMBER404_1",
                                      "message": "존재하지 않는 회원입니다.",
                                      "result": null
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
                    # 스토리북 목록 조회
                    
                    로그인한 회원의 스토리북 목록과 진행 상태, 상황별 추천 5개 카테고리를 조회합니다.
                    
                    ## 요청 형식
                    - **Header**
                        - Authorization: Bearer {accessToken}
                    
                    ## 동작 방식
                    1. 전체 스토리북을 storybookId 순서대로 조회합니다.
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
                                            "shortDescription": "가족과의 만남",
                                            "imageUrl": "https://example.com/storybook1.png",
                                            "status": "COMPLETED",
                                            "lastChapterOrder": 10,
                                            "startedDate": "2026-07-05",
                                            "lastCompletedDate": "2026-07-14"
                                          },
                                          {
                                            "storybookId": 2,
                                            "title": "당신 사용설명서",
                                            "shortDescription": "나를 아는 시간",
                                            "imageUrl": "https://example.com/storybook2.png",
                                            "status": "NOT_STARTED",
                                            "lastChapterOrder": null,
                                            "startedDate": null,
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "accessToken 만료·유효하지 않음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "AUTH401_1",
                                      "message": "토큰이 만료되었습니다.",
                                      "result": null
                                    }
                                    """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 회원",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "MEMBER404_1",
                                      "message": "존재하지 않는 회원입니다.",
                                      "result": null
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
                    # 스토리북 상세 조회
                    
                    스토리북 상세 정보와 10개 장 목록(진행 상태 포함)을 조회합니다.
                    
                    ## 요청 형식
                    - **Header**
                        - Authorization: Bearer {accessToken}
                    - **Path Variable**
                        - storybookId : 조회할 스토리북 ID
                    
                    ## 동작 방식
                    1. storybookId로 스토리북을 조회합니다. 존재하지 않으면 404(STORYBOOK404_1)를 반환합니다.
                    2. 회원의 장별 완료 여부를 조회합니다.
                    3. member_storybook의 lastCompletedDate로 오늘 완료 여부를 확인합니다.
                    4. 각 장의 상태(COMPLETED/TODAY/TODAY_LOCKED/LOCKED)를 계산하여 반환합니다.
                    5. 완료한 장 수를 기준으로 전체 진행률(%)을 계산하여 함께 반환합니다.
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "전체 완료된 스토리북",
                                            value = """
                        {
                          "isSuccess": true,
                          "code": "STORYBOOK200_2",
                          "message": "스토리북 상세를 성공적으로 조회했습니다.",
                          "result": {
                            "storybookId": 1,
                            "title": "오래 전 당신",
                            "description": "부모님을 '부모'가 아닌 한 사람으로 바라보며, 어린 시절의 기억과 청춘의 순간, 지나온 시간을 차근차근 들어보는 이야기입니다.",
                            "imageUrl": "https://example.com/storybook1.png",
                            "completedChapterCount": 10,
                            "progressPercentage": 100,
                            "chapters": [
                              { "chapterOrder": 1, "memberChapterId": 1, "title": "집 앞 한 바퀴", "shortImageUrl": "https://example.com/chapter1.png", "shortDescription": "부모님이 지금의 내 나이였을 때의 하루", "description": "부담 없이 집 앞을 함께 걸어보는 첫 장입니다.", "status": "COMPLETED" },
                              { "chapterOrder": 2, "memberChapterId": 2, "title": "자주 가던 길", "shortImageUrl": "https://example.com/chapter2.png", "shortDescription": "목차 예시2", "description": "부모님이 자주 가는 길을 함께 따라가봅니다.", "status": "COMPLETED" },
                              { "chapterOrder": 3, "memberChapterId": 3, "title": "부모님의 속도", "shortImageUrl": "https://example.com/chapter3.png", "shortDescription": "목차 예시3", "description": "부모님의 속도에 맞춰 걸어봐요.", "status": "COMPLETED" },
                              { "chapterOrder": 4, "memberChapterId": 4, "title": "함께 장보기", "shortImageUrl": "https://example.com/chapter4.png", "shortDescription": "목차 예시4", "description": "부모님과 장을 보며 선택의 기준을 알아봅니다.", "status": "COMPLETED" },
                              { "chapterOrder": 5, "memberChapterId": 5, "title": "가까운 외출", "shortImageUrl": "https://example.com/chapter5.png", "shortDescription": "목차 예시5", "description": "멀리 가지 않아도 가능한 작은 외출을 다녀옵니다.", "status": "COMPLETED" },
                              { "chapterOrder": 6, "memberChapterId": 6, "title": "오래된 장소", "shortImageUrl": "https://example.com/chapter6.png", "shortDescription": "목차 예시6", "description": "부모님에게 의미 있는 장소를 찾아가봅니다.", "status": "COMPLETED" },
                              { "chapterOrder": 7, "memberChapterId": 7, "title": "오늘의 사진 한 장", "shortImageUrl": "https://example.com/chapter7.png", "shortDescription": "목차 예시7", "description": "외출 중 부모님의 모습을 사진으로 남기는 장입니다.", "status": "COMPLETED" },
                              { "chapterOrder": 8, "memberChapterId": 8, "title": "조금 먼 곳", "shortImageUrl": "https://example.com/chapter8.png", "shortDescription": "목차 예시8", "description": "평소보다 조금 먼 곳을 함께 다녀옵니다.", "status": "COMPLETED" },
                              { "chapterOrder": 9, "memberChapterId": 9, "title": "돌아오는 길", "shortImageUrl": "https://example.com/chapter9.png", "shortDescription": "목차 예시9", "description": "돌아오는 길에 오늘의 감상을 나눠봅니다.", "status": "COMPLETED" },
                              { "chapterOrder": 10, "memberChapterId": 10, "title": "나란히 걷는 우리", "shortImageUrl": "https://example.com/chapter10.png", "shortDescription": "목차 예시10", "description": "함께 걸었던 길들을 돌아보는 마지막 장입니다.", "status": "COMPLETED" }
                            ]
                          }
                        }
                        """
                                    ),
                                    @ExampleObject(
                                            name = "진행 중인 스토리북",
                                            value = """
                {
                  "isSuccess": true,
                  "code": "STORYBOOK200_2",
                  "message": "스토리북 상세를 성공적으로 조회했습니다.",
                  "result": {
                    "storybookId": 4,
                    "title": "취향이 닿는 날",
                    "description": "좋아하는 것과 싫어하는 것을 하나씩 나누며, 서로의 취향이 닿는 순간들을 발견하는 이야기입니다.",
                    "imageUrl": "https://example.com/storybook4.png",
                    "completedChapterCount": 1,
                    "progressPercentage": 10,
                    "chapters": [
                      { "chapterOrder": 1, "memberChapterId": 1, "title": "나와 같은 나이였던 시절", "shortImageUrl": "https://example.com/chapter1-short.png", "shortDescription": "부모님이 지금의 내 나이였을 때의 하루", "description": "부모님을 한 사람으로 바라보는 첫 장입니다.", "status": "COMPLETED" },
                      { "chapterOrder": 2, "memberChapterId": null, "title": "소년과 소녀의 꿈", "shortImageUrl": "https://example.com/chapter2-short.png", "shortDescription": "목차 예시2", "description": "부모님에게도 작고 선명했던 어린 시절의 꿈이 있었습니다.", "status": "TODAY" },
                      { "chapterOrder": 3, "memberChapterId": null, "title": "목차 예시3", "shortImageUrl": "https://example.com/chapter3-short.png", "shortDescription": "목차 예시3", "description": "목차 예시3 설명입니다.", "status": "LOCKED" },
                      { "chapterOrder": 4, "memberChapterId": null, "title": "목차 예시4", "shortImageUrl": "https://example.com/chapter4-short.png", "shortDescription": "목차 예시4", "description": "목차 예시4 설명입니다.", "status": "LOCKED" },
                      { "chapterOrder": 5, "memberChapterId": null, "title": "목차 예시5", "shortImageUrl": "https://example.com/chapter5-short.png", "shortDescription": "목차 예시5", "description": "목차 예시5 설명입니다.", "status": "LOCKED" },
                      { "chapterOrder": 6, "memberChapterId": null, "title": "목차 예시6", "shortImageUrl": "https://example.com/chapter6-short.png", "shortDescription": "목차 예시6", "description": "목차 예시6 설명입니다.", "status": "LOCKED" },
                      { "chapterOrder": 7, "memberChapterId": null, "title": "목차 예시7", "shortImageUrl": "https://example.com/chapter7-short.png", "shortDescription": "목차 예시7", "description": "목차 예시7 설명입니다.", "status": "LOCKED" },
                      { "chapterOrder": 8, "memberChapterId": null, "title": "목차 예시8", "shortImageUrl": "https://example.com/chapter8-short.png", "shortDescription": "목차 예시8", "description": "목차 예시8 설명입니다.", "status": "LOCKED" },
                      { "chapterOrder": 9, "memberChapterId": null, "title": "목차 예시9", "shortImageUrl": "https://example.com/chapter9-short.png", "shortDescription": "목차 예시9", "description": "목차 예시9 설명입니다.", "status": "LOCKED" },
                      { "chapterOrder": 10, "memberChapterId": null, "title": "목차 예시10", "shortImageUrl": "https://example.com/chapter10-short.png", "shortDescription": "목차 예시10", "description": "목차 예시10 설명입니다.", "status": "LOCKED" }
                    ]
                  }
                }
                """
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "accessToken 만료·유효하지 않음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "AUTH401_1",
                                      "message": "토큰이 만료되었습니다.",
                                      "result": null
                                    }
                                    """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 회원 또는 스토리북",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "존재하지 않는 회원",
                                            value = """
                                                    {
                                                      "isSuccess": false,
                                                      "code": "MEMBER404_1",
                                                      "message": "존재하지 않는 회원입니다.",
                                                      "result": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "존재하지 않는 스토리북",
                                            value = """
                                                    {
                                                      "isSuccess": false,
                                                      "code": "STORYBOOK404_1",
                                                      "message": "존재하지 않는 스토리북입니다.",
                                                      "result": null
                                                    }
                                                    """
                                    )
                            }
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
                    # 스토리북 시작하기
                    
                    선택한 스토리북을 시작합니다.
                    
                    ## 요청 형식
                    - **Header**
                        - Authorization: Bearer {accessToken}
                    - **Path Variable**
                        - storybookId : 시작할 스토리북 ID
                    
                    ## 동작 방식
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
                    responseCode = "401",
                    description = "accessToken 만료·유효하지 않음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "AUTH401_1",
                                      "message": "토큰이 만료되었습니다.",
                                      "result": null
                                    }
                                    """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 회원 또는 스토리북",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "존재하지 않는 회원",
                                            value = """
                                                    {
                                                      "isSuccess": false,
                                                      "code": "MEMBER404_1",
                                                      "message": "존재하지 않는 회원입니다.",
                                                      "result": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "존재하지 않는 스토리북",
                                            value = """
                                                    {
                                                      "isSuccess": false,
                                                      "code": "STORYBOOK404_1",
                                                      "message": "존재하지 않는 스토리북입니다.",
                                                      "result": null
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이미 진행중이거나 이미 완료한 스토리북",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "이미 진행 중인 스토리북",
                                            value = """
                                                    {
                                                      "isSuccess": false,
                                                      "code": "STORYBOOK409_1",
                                                      "message": "이미 시작한 스토리북입니다.",
                                                      "result": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "이미 완료한 스토리북",
                                            value = """
                                                    {
                                                      "isSuccess": false,
                                                      "code": "STORYBOOK409_2",
                                                      "message": "이미 완료한 스토리북은 다시 시작할 수 없습니다.",
                                                      "result": null
                                                    }
                                                    """
                                    )
                            }
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
                    # 추천 스토리북 조회
                    
                    온보딩 시 선택한 목적 태그를 기반으로 추천 스토리북 2개를 조회합니다.
                    
                    ## 요청 형식
                    - **Header**
                        - Authorization: Bearer {accessToken}
                    
                    ## 동작 방식
                    1. 회원이 온보딩에서 선택한 목적 태그를 조회합니다.
                    2. 태그와 매칭되는 스토리북을 우선순위(PRIMARY > SECONDARY) 순으로 정렬합니다.
                    3. 매칭된 스토리북이 2개 미만이면 storybookId 순서를 기준으로 나머지를 채웁니다.
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "accessToken 만료·유효하지 않음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "AUTH401_1",
                                      "message": "토큰이 만료되었습니다.",
                                      "result": null
                                    }
                                    """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 회원",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "MEMBER404_1",
                                      "message": "존재하지 않는 회원입니다.",
                                      "result": null
                                    }
                                    """)
                    )
            )
    })
    ApiResponse<StorybookResDTO.GetRecommendedStorybooks> getRecommendedStorybooks(
            @AuthenticationPrincipal Long memberId
    );
}