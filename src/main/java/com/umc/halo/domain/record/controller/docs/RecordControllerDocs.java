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
                            description = "요청 값 누락 또는 형식 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "storybookChapterId 누락",
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
                                                    name = "선택한 커버 타입과 입력값 불일치",
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
                                                    name = "장면 카드(sceneCardId)가 해당 장의 장면 카드 아님",
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
                                                    name = "질문(answers[].chapterQuestionId)이 해당 장의 질문 아님",
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
                                                    name = "COMPLETED인데 모든 장 질문에 대한 답변 누락",
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
                                                    name = "COMPLETED인데 coverType 누락",
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
                                                    name = "COMPLETED인데 emotion 누락",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "CHAPTER400_6",
                                                                "message": "감정 선택이 필요합니다.",
                                                                "result": null
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "answers가 최대 개수(3개) 초과",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "COMMON400_1",
                                                                "message": "잘못된 요청입니다.",
                                                                "result": {
                                                                    "answers": "답변은 최대 3개까지 작성할 수 있습니다."
                                                                }
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "status 누락",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "COMMON400_1",
                                                                "message": "잘못된 요청입니다.",
                                                                "result": {
                                                                    "status": "status는 필수입니다."
                                                                }
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "answers[].chapterQuestionId 누락",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "COMMON400_1",
                                                                "message": "잘못된 요청입니다.",
                                                                "result": {
                                                                    "answers[0].chapterQuestionId": "chapterQuestionId는 필수입니다."
                                                                }
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "answers[].answer가 빈 값",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "COMMON400_1",
                                                                "message": "잘못된 요청입니다.",
                                                                "result": {
                                                                    "answers[0].answer": "답변 내용은 비어 있을 수 없습니다."
                                                                }
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "이미지 용량 제한 초과",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "IMAGE400_2",
                                                                "message": "이미지 용량이 제한을 초과했습니다.(최대 10MB)",
                                                                "result": null
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
                            description = "아직 열리지 않았거나 이미 완료한 장 / 본인이 업로드한 이미지가 아님",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "아직 열리지 않은 장",
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
                                                    name = "이미 완료한 장",
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
                                                    name = "본인이 업로드한 이미지가 아님(imageKey)",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "IMAGE403_1",
                                                                "message": "본인이 업로드한 이미지가 아닙니다.",
                                                                "result": null
                                                            }
                                                            """
                                            )
                                    }
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 회원 / 장 / 장 질문 / 장면 카드 / S3 이미지",
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
                                                    name = "존재하지 않는 장(storybookChapterId)",
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
                                                    name = "존재하지 않는 장 질문(answers[].chapterQuestionId)",
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
                                                    name = "존재하지 않는 장면 카드(sceneCardId)",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "CHAPTER404_3",
                                                                "message": "존재하지 않는 장면 카드입니다.",
                                                                "result": null
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "S3에 해당 이미지가 존재하지 않음(imageKey)",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "IMAGE404_1",
                                                                "message": "S3에 해당 이미지가 존재하지 않습니다.",
                                                                "result": null
                                                            }
                                                            """
                                            )
                                    }
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "409",
                            description = "오늘 이미 이 스토리북의 장을 완료했거나, 동시 요청으로 인한 저장 충돌",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "오늘 이미 이 스토리북의 장을 완료함",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "CHAPTER409_1",
                                                                "message": "오늘 이미 이 스토리북의 장을 완료하여 장 기록을 작성할 수 없습니다.",
                                                                "result": null
                                                            }
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "동시 요청으로 인한 저장 충돌",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "CHAPTER409_2",
                                                                "message": "동시에 처리된 요청으로 인한 충돌입니다. 다시 시도해주세요.",
                                                                "result": null
                                                            }
                                                            """
                                            )
                                    }
                            )
                    )
            }
    )
    ApiResponse<RecordResDTO.WriteChapterRecord> writeChapterRecord(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody RecordReqDTO.WriteChapterRecord recordReqDTO
    );


    // 완료된 장 다시보기
    @Operation(
            summary = "완료된 장 다시보기 API",
            description = """
                    # 완료된 장 다시보기
                    
                    ## 요청 형식
                    - 헤더: Authorization: Bearer {JWT 토큰}
                    - memberChapterId: 사용자 장 ID (path variable)

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
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "isSuccess": true,
                                                      "code": "CHAPTER200_3",
                                                      "message": "완료된 장을 성공적으로 조회했습니다.",
                                                      "result": {
                                                        "memberChapterId": 10,
                                                        "storybookTitle": "오래 전 당신",
                                                        "chapterTitle": "나와 같은 나이였던 시절",
                                                        "chapterOrder": 1,
                                                        "description": "부모님을 한 사람으로 바라보는 첫 장입니다. 지금의 내 나이였을 때 부모님은 어떤 하루를 살고 있었는지 돌아봅니다.",
                                                        "chapterImageUrl": "https://example.com/chapter1.png",
                                                        "emotion": "HAPPY",
                                                        "coverType": "SCENE_CARD",
                                                        "imageUrl": null,
                                                        "sceneCardImageUrl": "https://example.com/scene1.png",
                                                        "answers": [
                                                          { "question": "다시 쓰는 당신의 프로필에 대한 질문1", "answer": "답변1" },
                                                          { "question": "다시 쓰는 당신의 프로필에 대한 질문2", "answer": "답변2" },
                                                          { "question": "다시 쓰는 당신의 프로필에 대한 질문3", "answer": "답변3" }
                                                        ],
                                                        "completedDate": "2026-07-10"
                                                      }
                                                    }
                                                    """
                                    )
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
                            description = "장 기록이 아직 완료(COMPLETED) 상태가 아님",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject("""
                                            {
                                                "isSuccess": false,
                                                "code": "CHAPTER403_4",
                                                "message": "아직 완료되지 않은 장 기록입니다.",
                                                "result": null
                                            }
                                            """)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 회원 또는 장 기록(본인 소유가 아닌 기록 포함)",
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
                                                    name = "존재하지 않거나 본인 소유가 아닌 장 기록(memberChapterId)",
                                                    value = """
                                                            {
                                                                "isSuccess": false,
                                                                "code": "CHAPTER404_4",
                                                                "message": "존재하지 않는 장 기록입니다.",
                                                                "result": null
                                                            }
                                                            """
                                            )
                                    }
                            )
                    )
            }
    )
    ApiResponse<RecordResDTO.ReadChapterRecord> readChapterRecord(
            @AuthenticationPrincipal Long memberId,
            @Parameter(description = "다시 볼 장 기록 ID", example = "10")
            @PathVariable Long memberChapterId);
}


