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
                    - 헤더: Authorization: Bearer {JWT 토큰}
                    - storybookId: 조회할 스토리북 ID
                    - chapterOrder: 조회할 장 순서 (1~10)
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
                                                                "guide": "부모님이 지금 내 나이였을 땐 어떤 하루를 보냈을까요? 사진 한 장을 보며 가볍게 물어봐요.",
                                                                "character": {
                                                                  "originalUrl": "https://example.com/character-original.png",
                                                                  "imageChoiceUrl": "https://example.com/character-image-choice.png"
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
                                                                "guide": "부모님이 지금 내 나이였을 땐 어떤 하루를 보냈을까요? 사진 한 장을 보며 가볍게 물어봐요.",
                                                                "character": {
                                                                  "originalUrl": "https://example.com/character-original.png",
                                                                  "imageChoiceUrl": "https://example.com/character-image-choice.png"
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
                                                    name = "스토리북에 ORIGINAL/IMAGE_CHOICE 캐릭터가 등록되지 않음",
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