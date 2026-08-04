package com.umc.halo.domain.content.chapter.controller.docs;

import com.umc.halo.domain.content.chapter.dto.*;
import com.umc.halo.global.apiPayload.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.validation.constraints.*;
import org.springframework.security.core.annotation.*;
import org.springframework.web.bind.annotation.*;

@Tag(name = "장 API")
public interface ChapterControllerDocs {

    //오늘의 장 조회
    @Operation(
            summary = "오늘의 장 조회 API",
            description = """
                    # 오늘의 장 조회
                    
                    ## 요청 형식
                    - **Header**
                        - Authorization: Bearer {JWT 토큰}
                    - **Path Variable**
                        - storybookId : 조회할 스토리북 ID
                        - chapterOrder : 조회할 장 순서 (1~10)
                    
                    ## 동작 방식
                    1. storybookId와 chapterOrder로 장을 조회합니다. 존재하지 않으면 404(CHAPTER404_1)를 반환합니다.
                    2. 회원의 스토리북 진행 정보(MemberStorybook)를 조회합니다.
                    3. 오늘 이미 이 스토리북의 장을 완료했다면 403(CHAPTER403_3)을 반환합니다.
                    4. 진행 중인 장 순서를 기준으로 요청한 장에 접근 가능한지 검증합니다. 아직 열리지 않은 장이면 403(CHAPTER403_1), 이미 완료한 장이면 403(CHAPTER403_2)을 반환합니다.
                    5. 스토리북의 캐릭터(WRITING/SCENE_CARD), 질문 목록, 장면 카드를 함께 조회합니다.
                    6. 임시저장(draft) 답변이 있으면 함께 조회하고, imageKey가 있으면 presigned imageUrl을 새로 발급합니다.
                    7. 오늘의 장 정보를 반환합니다.
                    
                    ## 참고
                    - 응답의 imageUrl은 매 요청마다 새로 발급되는 presigned GET URL이며, 발급 후 1시간 동안만 유효합니다. 캐싱하지 말고 응답받은 URL을 바로 사용해주세요.
                    """,
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "임시저장(draft) 있음",
                                                    value = """
                                                            {
                                                              "isSuccess": true,
                                                              "code": "CHAPTER200_1",
                                                              "message": "오늘의 장을 성공적으로 조회했습니다.",
                                                              "result": {
                                                                "chapterId": 1,
                                                                "storybookTitle": "오래 전 당신",
                                                                "longImageUrl": "https://example.com/long-image.png",
                                                                "guide": "부모님이 지금 내 나이였을 땐 어떤 하루를 보냈을까요? 사진 한 장을 보며 가볍게 물어봐요.",
                                                                "character": {
                                                                  "writingCharacterImageUrl": "https://example.com/character-writing.png",
                                                                  "sceneCardCharacterImageUrl": "https://example.com/character-scene-card.png"
                                                                },
                                                                "questions": [
                                                                  { "chapterQuestionId": 1, "questionOrder": 1, "question": "나와 같은 나이였던 시절에 대한 질문1" },
                                                                  { "chapterQuestionId": 2, "questionOrder": 2, "question": "나와 같은 나이였던 시절에 대한 질문2" },
                                                                  { "chapterQuestionId": 3, "questionOrder": 3, "question": "나와 같은 나이였던 시절에 대한 질문3" }
                                                                ],
                                                                "sceneCards": [
                                                                  { "sceneCardId": 1, "imageUrl": "https://example.com/scene1.png" },
                                                                  { "sceneCardId": 2, "imageUrl": "https://example.com/scene2.png" },
                                                                  { "sceneCardId": 3, "imageUrl": "https://example.com/scene3.png" },
                                                                  { "sceneCardId": 4, "imageUrl": "https://example.com/scene4.png" }
                                                                ],
                                                                "draft": {
                                                                  "status": "DRAFT",
                                                                  "answers": [
                                                                    { "chapterQuestionId": 1, "questionOrder": 1, "answer": "나와 같은 나이였던 시절에 대한 답변1" },
                                                                    { "chapterQuestionId": 2, "questionOrder": 2, "answer": "나와 같은 나이였던 시절에 대한 답변2" }
                                                                  ],
                                                                  "coverType": null,
                                                                  "imageUrl": null,
                                                                  "imageKey": null,
                                                                  "sceneCardId": null,
                                                                  "emotion": null
                                                                }
                                                              }
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "아직 시작하지 않은 장 (draft 없음)",
                                                    value = """
                                                            {
                                                              "isSuccess": true,
                                                              "code": "CHAPTER200_1",
                                                              "message": "오늘의 장을 성공적으로 조회했습니다.",
                                                              "result": {
                                                                "chapterId": 1,
                                                                "storybookTitle": "오래 전 당신",
                                                                "longImageUrl": "https://example.com/long-image.png",
                                                                "guide": "부모님이 지금 내 나이였을 땐 어떤 하루를 보냈을까요? 사진 한 장을 보며 가볍게 물어봐요.",
                                                                "character": {
                                                                  "writingCharacterImageUrl": "https://example.com/character-writing.png",
                                                                  "sceneCardCharacterImageUrl": "https://example.com/character-scene-card.png"
                                                                },
                                                                "questions": [
                                                                  { "chapterQuestionId": 1, "questionOrder": 1, "question": "나와 같은 나이였던 시절에 대한 질문1" },
                                                                  { "chapterQuestionId": 2, "questionOrder": 2, "question": "나와 같은 나이였던 시절에 대한 질문2" },
                                                                  { "chapterQuestionId": 3, "questionOrder": 3, "question": "나와 같은 나이였던 시절에 대한 질문3" }
                                                                ],
                                                                "sceneCards": [
                                                                  { "sceneCardId": 1, "imageUrl": "https://example.com/scene1.png" },
                                                                  { "sceneCardId": 2, "imageUrl": "https://example.com/scene2.png" },
                                                                  { "sceneCardId": 3, "imageUrl": "https://example.com/scene3.png" },
                                                                  { "sceneCardId": 4, "imageUrl": "https://example.com/scene4.png" }
                                                                ],
                                                                "draft": {
                                                                  "status": "NONE",
                                                                  "answers": [],
                                                                  "coverType": null,
                                                                  "imageUrl": null,
                                                                  "imageKey": null,
                                                                  "sceneCardId": null,
                                                                  "emotion": null
                                                                }
                                                              }
                                                            }
                                                            """
                                            )
                                    }
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "chapterOrder가 1~10 범위를 벗어남",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "chapterOrder가 1 미만",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "COMMON400_1",
                                                                "message": "잘못된 요청입니다.",
                                                                "result": {
                                                                    "chapterOrder": "장 순서는 1 이상이어야 합니다."
                                                                }
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "chapterOrder가 10 초과",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "COMMON400_1",
                                                                "message": "잘못된 요청입니다.",
                                                                "result": {
                                                                    "chapterOrder": "장 순서는 10 이하여야 합니다."
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
                                    schema = @Schema(implementation = ApiResponse.class),
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
                            responseCode = "403",
                            description = "아직 열리지 않았거나 이미 완료한 장",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "아직 열리지 않은 장 (순서상 다음 장이 아님)",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "CHAPTER403_1",
                                                                "message": "아직 열리지 않은 장입니다.",
                                                                "result": null
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "이미 완료한 장 재조회 시도",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "CHAPTER403_2",
                                                                "message": "이미 완료한 장입니다.",
                                                                "result": null
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "오늘 이미 이 스토리북의 장을 완료하여 새 장 조회 불가",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "CHAPTER403_3",
                                                                "message": "오늘 이미 이 스토리북의 장을 완료하여 새로운 장을 조회할 수 없습니다.",
                                                                "result": null
                                                            }
                                                            """
                                            )
                                    }
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 회원/스토리북/장/캐릭터",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
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
                                                    name = "존재하지 않는 스토리북이거나 해당 스토리북에 없는 chapterOrder",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "CHAPTER404_1",
                                                                "message": "존재하지 않는 장입니다.",
                                                                "result": null
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "스토리북에 WRITING/SCENE_CARD 캐릭터가 등록되지 않음",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "STORYBOOK404_2",
                                                                "message": "존재하지 않는 캐릭터입니다.",
                                                                "result": null
                                                            }
                                                            """
                                            )
                                    }
                            )
                    )
            }
    )
    ApiResponse<ChapterResDTO.TodayChapter> getTodayChapter(
            @AuthenticationPrincipal Long memberId,
            @Parameter(description = "스토리북 ID", example = "1")
            @PathVariable Long storybookId,
            @Parameter(description = "장 순서 (1~10)", example = "1")
            @PathVariable
            @Min(value = 1, message = "장 순서는 1 이상이어야 합니다.")
            @Max(value = 10, message = "장 순서는 10 이하여야 합니다.") Integer chapterOrder);

}