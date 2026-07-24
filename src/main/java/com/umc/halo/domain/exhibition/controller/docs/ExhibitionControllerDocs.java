package com.umc.halo.domain.exhibition.controller.docs;

import com.umc.halo.domain.exhibition.dto.ExhibitionChapterResDTO;
import com.umc.halo.domain.exhibition.dto.ExhibitionResDTO;
import com.umc.halo.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "테마함 API")
public interface ExhibitionControllerDocs {

    @Operation(
            summary = "테마함 조회 API",
            description = """
                    # 테마함 메인 조회
                    
                    ## 요청 형식
                    - 헤더: Authorization: Bearer {JWT 토큰}
                    
                    수집한 캐릭터 수·진행중 스토리북 수와 함께 아래 중 하나의 목록을 반환합니다.
                    - 완료한 스토리북이 있으면: storybooks (+ currentStorybookId)
                    - 완료는 없고 진행중만 있으면: inProgressStorybooks
                    - 둘 다 없으면: recommendedStorybooks
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공 예시 (완료한 스토리북 존재)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "isSuccess": true,
                              "code": "EXHIBITION200_1",
                              "message": "테마함 조회를 성공했습니다.",
                              "result": {
                                "stats": { "collectedCharacterCount": 3, "inProgressStorybookCount": 2 },
                                "currentStorybookId": 101,
                                "storybooks": [
                                  {
                                    "storybookId": 101,
                                    "order": 1,
                                    "title": "오래 전 당신",
                                    "summary": "가족과의 만남",
                                    "completedDate": "2026-06-10",
                                    "characterId": 1,
                                    "characterName": "할로로",
                                    "characterImageUrl": "https://image101"
                                  }
                                ],
                                "inProgressStorybooks": [],
                                "recommendedStorybooks": []
                              }
                            }
                            """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - JWT 토큰 미삽입/만료",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            { "isSuccess": false, "code": "AUTH401_1", "message": "토큰이 만료되었습니다.", "result": null }
                            """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "토큰의 회원이 존재하지 않음 (탈퇴 등)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            { "isSuccess": false, "code": "MEMBER404_1", "message": "존재하지 않는 회원입니다.", "result": null }
                            """))
            )
    })
    ApiResponse<ExhibitionResDTO.MainInfo> getExhibition(
            @Parameter(hidden = true) Long memberId
    );

    @Operation(
            summary = "스토리북 감상(장 목록) 조회 API",
            description = """
                    # 스토리북 감상(장 목록) 조회
                    
                    ## 요청 형식
                    - 헤더: Authorization: Bearer {JWT 토큰}
                    - 경로 변수: storybookId
                    
                    완료한 스토리북의 전체 장을 순서대로 한 번에 조회합니다.
                    완료되지 않은 스토리북은 감상할 수 없습니다.
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "isSuccess": true,
                              "code": "EXHIBITION200_2",
                              "message": "스토리북 감상 조회를 성공했습니다.",
                              "result": {
                                "storybookId": 101,
                                "chapters": [
                                  {
                                    "chapterOrder": 1,
                                    "chapterImageUrl": "https://image1",
                                    "title": "나와 같은 나이였던 시절",
                                    "summary": "답변 3개 ai로 요약한거",
                                    "completedDate": "2026-06-30",
                                    "emotion": "HAPPY"
                                  }
                                ]
                              }
                            }
                            """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - JWT 토큰 미삽입/만료",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            { "isSuccess": false, "code": "AUTH401_1", "message": "토큰이 만료되었습니다.", "result": null }
                            """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "아직 완료되지 않은(진행중) 스토리북",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            { "isSuccess": false, "code": "EXHIBITION403_1", "message": "아직 완료되지 않은 스토리북입니다.", "result": null }
                            """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않거나 접근 권한이 없는 전시 기록",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            { "isSuccess": false, "code": "EXHIBITION404_1", "message": "존재하지 않거나 접근 권한이 없는 전시 기록입니다.", "result": null }
                            """))
            )
    })
    ApiResponse<ExhibitionChapterResDTO.ChaptersInfo> getChapters(
            @Parameter(hidden = true) Long memberId,
            @Parameter(description = "조회할 스토리북 ID", example = "101") Long storybookId
    );
}