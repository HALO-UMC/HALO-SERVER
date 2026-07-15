package com.umc.halo.domain.record.controller.docs;

import com.umc.halo.domain.record.dto.*;
import com.umc.halo.global.apiPayload.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.validation.*;
import org.springframework.security.core.annotation.*;
import org.springframework.web.bind.annotation.*;

@Tag(name = "장 API")
public interface RecordControllerDocs {

    // 장 기록 작성
    @Operation(
            summary = "장 기록 작성 API",
            description = """
                    # 장 기록 작성
                    
                    ## 요청 형식
                    - 헤더: Authorization: Bearer {JWT 토큰}
                    - storybookChapterId: 기록할 스토리북-장 ID
                    - emotion / coverType: status가 COMPLETED일 때만 필수
                    - imageUrl / imageKey: coverType이 IMAGE일 때만 필수
                    - sceneCardId: coverType이 SCENE_CARD일 때만 필수
                    - answers: 질문별 답변 목록 (최대 3개, 단계별 저장 시 일부만 포함 가능. status가 COMPLETED일 때는 장의 모든 질문에 대한 답변 필요)
                    - status: DRAFT(임시저장) 또는 COMPLETED(최종완료)
                    """,
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            name = "장 기록 저장 성공",
                                            value = """
                                                    {
                                                      "isSuccess": true,
                                                      "code": "CHAPTER200_2",
                                                      "message": "장을 성공적으로 작성했습니다.",
                                                      "result": {
                                                        "memberChapterId": 10,
                                                        "isStorybookCompleted": false
                                                      }
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "요청 값이 올바르지 않은 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "storybookChapterId 등 필수 요청값이 누락된 경우",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "COMMON400_1",
                                                                "message": "잘못된 요청입니다.",
                                                                "result": {
                                                                    "storybookChapterId": "storybookChapterId는 필수입니다."
                                                                }
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "선택한 커버 타입과 입력값이 일치하지 않거나 필요한 정보가 누락된 경우",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "CHAPTER400_1",
                                                                "message": "선택한 커버 타입과 입력값이 일치하지 않습니다.",
                                                                "result": null
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "장면 카드(sceneCardId)가 해당 장의 장면 카드가 아닌 경우",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "CHAPTER400_2",
                                                                "message": "해당 장의 장면 카드가 아닙니다.",
                                                                "result": null
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "질문(answers[].chapterQuestionId)이 해당 장의 질문이 아닌 경우",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "CHAPTER400_3",
                                                                "message": "해당 장의 질문이 아닙니다.",
                                                                "result": null
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "COMPLETED인데 모든 장 질문에 대한 답변이 없는 경우",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "CHAPTER400_4",
                                                                "message": "모든 장 질문에 대한 답변이 필요합니다.",
                                                                "result": null
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "COMPLETED인데 coverType이 없는 경우",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "CHAPTER400_5",
                                                                "message": "커버 타입 선택이 필요합니다.",
                                                                "result": null
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "COMPLETED인데 emotion이 없는 경우",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "CHAPTER400_6",
                                                                "message": "감정 선택이 필요합니다.",
                                                                "result": null
                                                            }
                                                            """
                                            )
                                    }
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "장이 아직 열리지 않았거나, 이미 완료한 장인 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "아직 열리지 않은 장인 경우",
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
                                                    name = "이미 완료한 장인 경우",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "CHAPTER403_2",
                                                                "message": "이미 완료한 장입니다.",
                                                                "result": null
                                                            }
                                                            """
                                            )
                                    }
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 회원 / 장 / 장 질문 / 장면 카드인 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "존재하지 않는 회원인 경우",
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
                                                    name = "존재하지 않는 장(storybookChapterId)인 경우",
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
                                                    name = "존재하지 않는 장 질문(answers[].chapterQuestionId)인 경우",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "CHAPTER404_2",
                                                                "message": "존재하지 않는 장 질문입니다.",
                                                                "result": null
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "존재하지 않는 장면 카드(sceneCardId)인 경우",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "CHAPTER404_3",
                                                                "message": "존재하지 않는 장면 카드입니다.",
                                                                "result": null
                                                            }
                                                            """
                                            )
                                    }
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "409",
                            description = "오늘 이미 이 스토리북의 장을 완료한 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject("""
                                            {
                                                "isSuccess": false,
                                                "code": "CHAPTER409_1",
                                                "message": "오늘 이미 이 스토리북의 장을 완료했습니다.",
                                                "result": null
                                            }
                                            """)
                            )
                    )
            }
    )
    ApiResponse<RecordResDTO.WriteChapterRecord> writeChapterRecord(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody RecordReqDTO.WriteChapterRecord recordReqDTO
    );
}


