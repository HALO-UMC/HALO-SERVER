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
                                            "bookshelf": [
                                              { "storybookId": 1, "title": "오래전 당신", "shortDescription": "가족과의 만남", "imageUrl": "https://example.com/storybook1.png", "currentChapterOrder": null, "todayAvailable": true, "recommendationReasonText": "부모님의 지난 시간을 알고 싶다면" },
                                              { "storybookId": 2, "title": "당신 사용설명서", "shortDescription": "따뜻했던 순간들", "imageUrl": "https://example.com/storybook2.png", "currentChapterOrder": null, "todayAvailable": true, "recommendationReasonText": "부모님을 더 잘 이해하고 싶다면" },
                                              { "storybookId": 3, "title": "가족의 온도", "shortDescription": "가족이 서로에게 건네는 마음", "imageUrl": "https://example.com/storybook3.png", "currentChapterOrder": 1, "todayAvailable": true, "recommendationReasonText": null },
                                              { "storybookId": 4, "title": "취향이 닿는 날", "shortDescription": "서로의 취향을 알아가는 시간", "imageUrl": "https://example.com/storybook4.png", "currentChapterOrder": 3, "todayAvailable": true, "recommendationReasonText": null },
                                              { "storybookId": 5, "title": "나란히 걷는 날", "shortDescription": "함께 걸어온 시간을 돌아보는 이야기", "imageUrl": "https://example.com/storybook5.png", "currentChapterOrder": null, "todayAvailable": true, "recommendationReasonText": "함께하는 시간을 늘리고 싶다면" },
                                              { "storybookId": 6, "title": "오늘은 내가 먼저", "shortDescription": "먼저 다가서는 용기에 대한 이야기", "imageUrl": "https://example.com/storybook6.png", "currentChapterOrder": null, "todayAvailable": true, "recommendationReasonText": "먼저 마음을 표현하고 싶다면" },
                                              { "storybookId": 7, "title": "생신까지 열 장", "shortDescription": "열 번의 준비", "imageUrl": "https://example.com/storybook7.png", "currentChapterOrder": null, "todayAvailable": true, "recommendationReasonText": "특별한 생신을 준비하고 싶다면" },
                                              { "storybookId": 8, "title": "한 장의 가족사진", "shortDescription": "한 장의 추억", "imageUrl": "https://example.com/storybook8.png", "currentChapterOrder": null, "todayAvailable": true, "recommendationReasonText": "가족의 순간을 남기고 싶다면" },
                                              { "storybookId": 9, "title": "손을 내미는 연습", "shortDescription": "용기의 시작", "imageUrl": "https://example.com/storybook9.png", "currentChapterOrder": null, "todayAvailable": true, "recommendationReasonText": "어색한 관계에 다가가고 싶다면" },
                                              { "storybookId": 10, "title": "당신의 1호 팬", "shortDescription": "가장 큰 응원", "imageUrl": "https://example.com/storybook10.png", "currentChapterOrder": null, "todayAvailable": true, "recommendationReasonText": "부모님을 응원하고 싶다면" }
                                            ],
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
                                            "bookshelf": [
                                              { "storybookId": 1, "title": "오래전 당신", "shortDescription": "가족과의 만남", "imageUrl": "https://example.com/storybook1.png", "currentChapterOrder": null, "todayAvailable": true, "recommendationReasonText": "부모님의 지난 시간을 알고 싶다면" },
                                              { "storybookId": 2, "title": "당신 사용설명서", "shortDescription": "따뜻했던 순간들", "imageUrl": "https://example.com/storybook2.png", "currentChapterOrder": null, "todayAvailable": true, "recommendationReasonText": "부모님을 더 잘 이해하고 싶다면" },
                                              { "storybookId": 3, "title": "가족의 온도", "shortDescription": "가족이 서로에게 건네는 마음", "imageUrl": "https://example.com/storybook3.png", "currentChapterOrder": null, "todayAvailable": true, "recommendationReasonText": "가족의 마음을 다시 느끼고 싶다면" },
                                              { "storybookId": 4, "title": "취향이 닿는 날", "shortDescription": "서로의 취향을 알아가는 시간", "imageUrl": "https://example.com/storybook4.png", "currentChapterOrder": null, "todayAvailable": true, "recommendationReasonText": "함께할 취향을 찾고 싶다면" },
                                              { "storybookId": 5, "title": "나란히 걷는 날", "shortDescription": "함께 걸어온 시간을 돌아보는 이야기", "imageUrl": "https://example.com/storybook5.png", "currentChapterOrder": null, "todayAvailable": true, "recommendationReasonText": "함께하는 시간을 늘리고 싶다면" },
                                              { "storybookId": 6, "title": "오늘은 내가 먼저", "shortDescription": "먼저 다가서는 용기에 대한 이야기", "imageUrl": "https://example.com/storybook6.png", "currentChapterOrder": null, "todayAvailable": true, "recommendationReasonText": "먼저 마음을 표현하고 싶다면" },
                                              { "storybookId": 7, "title": "생신까지 열 장", "shortDescription": "열 번의 준비", "imageUrl": "https://example.com/storybook7.png", "currentChapterOrder": null, "todayAvailable": true, "recommendationReasonText": "특별한 생신을 준비하고 싶다면" },
                                              { "storybookId": 8, "title": "한 장의 가족사진", "shortDescription": "한 장의 추억", "imageUrl": "https://example.com/storybook8.png", "currentChapterOrder": null, "todayAvailable": true, "recommendationReasonText": "가족의 순간을 남기고 싶다면" },
                                              { "storybookId": 9, "title": "손을 내미는 연습", "shortDescription": "용기의 시작", "imageUrl": "https://example.com/storybook9.png", "currentChapterOrder": null, "todayAvailable": true, "recommendationReasonText": "어색한 관계에 다가가고 싶다면" },
                                              { "storybookId": 10, "title": "당신의 1호 팬", "shortDescription": "가장 큰 응원", "imageUrl": "https://example.com/storybook10.png", "currentChapterOrder": null, "todayAvailable": true, "recommendationReasonText": "부모님을 응원하고 싶다면" }
                                            ],
                                            "inProgressStorybooks": [],
                                            "recommendedStorybooks": [
                                              {
                                                "storybookId": 1,
                                                "title": "오래전 당신",
                                                "shortDescription": "가족과의 만남",
                                                "imageUrl": "https://example.com/storybook1.png",
                                                "recommendationReasonText": "부모님의 지난 시간을 알고 싶다면"
                                              },
                                              {
                                                "storybookId": 2,
                                                "title": "당신 사용설명서",
                                                "shortDescription": "따뜻했던 순간들",
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
                                            "title": "오래전 당신",
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
                                            "shortDescription": "따뜻했던 순간들",
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
                                                "title": "오래전 당신",
                                                "imageUrl": "https://example.com/storybook1.png",
                                                "recommendationReasonText": "부모님의 지난 시간을 알고 싶다면"
                                              },
                                              {
                                                "storybookId": 8,
                                                "title": "한 장의 가족사진",
                                                "imageUrl": "https://example.com/storybook8.png",
                                                "recommendationReasonText": "가족의 순간을 남기고 싶다면"
                                              }
                                            ]
                                          },
                                          {
                                            "tag": "부모님을 더 알고 싶어요",
                                            "storybooks": [
                                              {
                                                "storybookId": 3,
                                                "title": "가족의 온도",
                                                "imageUrl": "https://example.com/storybook3.png",
                                                "recommendationReasonText": "가족의 마음을 다시 느끼고 싶다면"
                                              },
                                              {
                                                "storybookId": 2,
                                                "title": "당신 사용설명서",
                                                "imageUrl": "https://example.com/storybook2.png",
                                                "recommendationReasonText": "부모님을 더 잘 이해하고 싶다면"
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
                            "title": "오래전 당신",
                            "description": "부모님에게도 지금의 나와 같은 시절이 있었습니다. 어린 날의 꿈과 청춘의 순간을 따라가며, 부모가 아닌 한 사람으로서의 당신을 만나봅니다.",
                            "imageUrl": "https://example.com/storybook1.png",
                            "completedChapterCount": 10,
                            "progressPercentage": 100,
                            "chapters": [
                              { "chapterOrder": 1, "memberChapterId": 1, "title": "나와 같은 나이였던 시절", "shortImageUrl": "https://example.com/chapter1.png", "shortDescription": "부모님이 지금의 내 나이였을 때의 하루", "description": "부모님을 한 사람으로 바라보는 첫 장입니다. 지금의 내 나이였을 때 부모님은 어떤 하루를 살고 있었는지 돌아봅니다.", "status": "COMPLETED" },
                              { "chapterOrder": 2, "memberChapterId": 2, "title": "소년과 소녀의 꿈", "shortImageUrl": "https://example.com/chapter2.png", "shortDescription": "어린 시절 마음속에 품었던 작은 꿈들", "description": "부모님에게도 작고 선명했던 어린 시절의 꿈이 있었습니다. 무엇을 좋아하고 어떤 사람이 되고 싶었는지 들어봅니다.", "status": "COMPLETED" },
                              { "chapterOrder": 3, "memberChapterId": 3, "title": "빛나던 청춘의 한 페이지", "shortImageUrl": "https://example.com/chapter3.png", "shortDescription": "설렘과 도전으로 반짝이던 젊은 날", "description": "부모님의 젊은 날이 담긴 사진 속으로 들어가보는 장입니다. 사진 속 장소와 사람들을 통해 내가 몰랐던 부모님의 표정을 발견합니다.", "status": "COMPLETED" },
                              { "chapterOrder": 4, "memberChapterId": 4, "title": "첫 출근과 첫 월급", "shortImageUrl": "https://example.com/chapter4.png", "shortDescription": "처음 세상에 내디딘 사회인의 발걸음", "description": "부모님이 처음 사회에 나섰던 순간을 돌아봅니다. 첫 출근의 마음과 첫 월급의 기억을 통해 부모님의 시작점을 기록합니다.", "status": "COMPLETED" },
                              { "chapterOrder": 5, "memberChapterId": 5, "title": "두 사람의 서막", "shortImageUrl": "https://example.com/chapter5.png", "shortDescription": "두 사람이 만나 가족이 시작된 순간", "description": "부모님이 서로를 만나 가족이 시작되기 전의 이야기를 들어봅니다. 첫 만남과 첫인상을 통해 우리 가족의 첫 장면을 떠올립니다.", "status": "COMPLETED" },
                              { "chapterOrder": 6, "memberChapterId": 6, "title": "가장 용기 냈던 순간", "shortImageUrl": "https://example.com/chapter6.png", "shortDescription": "삶의 방향을 바꾸었던 가장 큰 선택", "description": "부모님이 삶에서 큰 결정을 내렸던 순간을 돌아봅니다. 그때의 고민과 선택을 들으며 부모님의 용기를 이해해봅니다.", "status": "COMPLETED" },
                              { "chapterOrder": 7, "memberChapterId": 7, "title": "잊지 못할 사람들", "shortImageUrl": "https://example.com/chapter7.png", "shortDescription": "오랜 시간이 지나도 남아 있는 인연들", "description": "부모님 마음속에 오래 남아 있는 사람을 만나보는 장입니다. 그 사람이 어떤 의미였는지 들으며 부모님의 관계와 시간을 알아갑니다.", "status": "COMPLETED" },
                              { "chapterOrder": 8, "memberChapterId": 8, "title": "부모가 되기 전의 밤", "shortImageUrl": "https://example.com/chapter8.png", "shortDescription": "부모라는 이름을 만나기 직전의 마음", "description": "내가 태어나기 전 부모님의 마음을 조심스럽게 들어봅니다. 기다림과 걱정, 설렘이 섞여 있던 시간을 기록합니다.", "status": "COMPLETED" },
                              { "chapterOrder": 9, "memberChapterId": 9, "title": "잠시 접어두었던 것들", "shortImageUrl": "https://example.com/chapter9.png", "shortDescription": "가족을 위해 잠시 미뤄두었던 꿈과 시간", "description": "부모님이 가족을 위해 잠시 미뤄두었던 것들을 떠올려봅니다. 내려놓았던 꿈이나 취미를 통해 부모님의 또 다른 모습을 발견합니다.", "status": "COMPLETED" },
                              { "chapterOrder": 10, "memberChapterId": 10, "title": "다시 쓰는 당신의 프로필", "shortImageUrl": "https://example.com/chapter10.png", "shortDescription": "부모가 아닌 한 사람으로 다시 만난 당신", "description": "앞의 기록을 바탕으로 부모님을 다시 소개하는 마지막 장입니다. 누구의 부모가 아닌 한 사람으로서의 부모님을 정리합니다.", "status": "COMPLETED" }
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
                    "description": "서로 좋아하는 것을 나누면 대화를 시작할 이유가 하나 더 생깁니다. 음식과 노래, 장소와 취미를 함께 경험하며 우리 둘의 취향이 닿는 순간을 만들어봅니다.",
                    "imageUrl": "https://example.com/storybook4.png",
                    "completedChapterCount": 1,
                    "progressPercentage": 10,
                    "chapters": [
                      { "chapterOrder": 1, "memberChapterId": 1, "title": "같이 먹어본맛", "shortImageUrl": "https://example.com/chapter1-short.png", "shortDescription": "한 끼를 나누며 발견한 닮은 입맛", "description": "부모님이 좋아하는 음식을 함께 먹어보는 장입니다. 맛보다 그 음식을 좋아하는 이유를 들으며 취향을 알아갑니다.", "status": "COMPLETED" },
                      { "chapterOrder": 2, "memberChapterId": null, "title": "부모님의 플레이리스트", "shortImageUrl": "https://example.com/chapter2-short.png", "shortDescription": "노래 한 곡에 담긴 부모님의 시간", "description": "부모님이 좋아하는 노래를 함께 들어봅니다. 노래에 담긴 시절과 분위기를 통해 부모님의 기억을 만납니다.", "status": "TODAY" },
                      { "chapterOrder": 3, "memberChapterId": null, "title": "같이 본 장면", "shortImageUrl": "https://example.com/chapter3-short.png", "shortDescription": "같은 장면을 보며 나눈 서로 다른 감상", "description": "부모님과 영상 콘텐츠를 함께 보는 장입니다. 어떤 장면을 좋아하셨는지 들으며 취향의 방향을 발견합니다.", "status": "LOCKED" },
                      { "chapterOrder": 4, "memberChapterId": null, "title": "취미의 입구", "shortImageUrl": "https://example.com/chapter4-short.png", "shortDescription": "부모님이 좋아하는 세계에 들어선 첫걸음", "description": "부모님의 취미를 아주 짧게 따라 해봅니다. 잘하려고 하기보다 왜 즐거운지 느껴보는 것이 중심입니다.", "status": "LOCKED" },
                      { "chapterOrder": 5, "memberChapterId": null, "title": "단골의 이유", "shortImageUrl": "https://example.com/chapter5-short.png", "shortDescription": "오래 찾게 되는 익숙한 장소의 이유", "description": "부모님이 자주 가는 장소에 함께 가봅니다. 그곳이 편한 이유와 부모님의 일상적인 시간을 알아갑니다.", "status": "LOCKED" },
                      { "chapterOrder": 6, "memberChapterId": null, "title": "고르는 기준", "shortImageUrl": "https://example.com/chapter6-short.png", "shortDescription": "물건 하나에도 드러나는 부모님의 기준", "description": "부모님이 물건을 고를 때 중요하게 보는 기준을 관찰합니다. 가격, 편함, 오래 씀 같은 선택의 이유를 기록합니다.", "status": "LOCKED" },
                      { "chapterOrder": 7, "memberChapterId": null, "title": "쉬는 날의 선택", "shortImageUrl": "https://example.com/chapter7-short.png", "shortDescription": "쉬는 날 자연스럽게 향하는 곳과 시간", "description": "부모님이 쉬는 날 하고 싶은 일을 함께 해봅니다. 거창하지 않아도 부모님의 리듬을 따라가보는 장입니다.", "status": "LOCKED" },
                      { "chapterOrder": 8, "memberChapterId": null, "title": "내 취향 소개하기", "shortImageUrl": "https://example.com/chapter8-short.png", "shortDescription": "내가 좋아하는 것을 부모님께 건넨 날", "description": "이번에는 내가 좋아하는 것을 부모님께 소개합니다. 부모님의 반응을 보며 의외로 닿는 취향을 찾아봅니다.", "status": "LOCKED" },
                      { "chapterOrder": 9, "memberChapterId": null, "title": "우리 둘의 공통점", "shortImageUrl": "https://example.com/chapter9-short.png", "shortDescription": "사소하지만 반가운 우리 둘의 닮은 점", "description": "부모님과 나 사이의 비슷한 취향을 발견합니다. 사소한 공통점도 다음 경험으로 이어질 수 있습니다.", "status": "LOCKED" },
                      { "chapterOrder": 10, "memberChapterId": null, "title": "같이좋아하게 된 것", "shortImageUrl": "https://example.com/chapter10-short.png", "shortDescription": "함께 경험하며 새로 생겨난 공통 취향", "description": "함께 경험한 취향들을 돌아보는 마지막 장입니다. 앞으로도 이어가고 싶은 작은 취향 루틴을 정리합니다.", "status": "LOCKED" }
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
                    
                    온보딩 시 선택한 목적 태그를 기반으로 추천 스토리북을 최대 2개 조회합니다. 이미 시작했거나 완료한 스토리북은 추천 후보에서 제외되며, 후보가 부족하면 2개보다 적게(0~1개) 반환될 수 있습니다.
                    
                    ## 요청 형식
                    - **Header**
                        - Authorization: Bearer {accessToken}
                    
                    ## 동작 방식
                    1. 회원이 이미 시작(진행중/완료)한 스토리북을 조회하여 추천 후보에서 제외합니다.
                    2. 회원이 온보딩에서 선택한 목적 태그(1~2개)를 조회합니다.
                    3. 태그가 1개면 해당 태그가 PRIMARY인 스토리북을 매칭합니다. 태그가 2개(X, Y)면 "PRIMARY=X이면서 SECONDARY=Y"인 스토리북과 "PRIMARY=Y이면서 SECONDARY=X"인 스토리북을 각각 매칭합니다.
                    4. 매칭된 스토리북이 2개 미만이면 storybookId 순서를 기준으로 나머지를 채웁니다(이미 시작/완료한 스토리북은 제외).
                    5. 최종 추천 스토리북을 최대 2개 반환합니다(후보가 부족하면 0~1개 반환될 수 있음).
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
                                            "storybookId": 3,
                                            "title": "가족의 온도",
                                            "shortDescription": "가족이 서로에게 건네는 마음",
                                            "imageUrl": "https://example.com/storybook3.png",
                                            "recommendationReasonText": "가족의 마음을 다시 느끼고 싶다면"
                                          },
                                          {
                                            "storybookId": 5,
                                            "title": "나란히 걷는 날",
                                            "shortDescription": "함께 걸어온 시간을 돌아보는 이야기",
                                            "imageUrl": "https://example.com/storybook5.png",
                                            "recommendationReasonText": "함께하는 시간을 늘리고 싶다면"
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