package com.umc.halo.domain.calendar.controller.docs;

import com.umc.halo.domain.calendar.dto.CalendarDailyResDTO;
import com.umc.halo.domain.calendar.dto.CalendarMonthlyResDTO;
import com.umc.halo.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Tag(name = "캘린더 API")
public interface CalendarControllerDocs {

    @Operation(
            summary = "캘린더 메인 조회 API",
            description = """
                    # 캘린더 메인 조회
                    
                    ## 요청 형식
                    - 헤더: Authorization: Bearer {JWT 토큰}
                    - 쿼리 파라미터: year, month(1~12)
                    
                    선택한 월의 완료 페이지 수, 기록이 있는 날짜, 완성/진행 중 스토리북 통계를 조회합니다.
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "isSuccess": true,
                              "code": "CALENDAR200_1",
                              "message": "캘린더 메인 조회를 성공했습니다.",
                              "result": {
                                "stats": { "completedPageCount": 30, "completedStorybookCount": 3, "inProgressStorybookCount": 2 },
                                "recordedDays": [9, 10, 11, 19],
                                "completedStorybooks": [
                                  { "storybookId": 1, "spineColor": "#F0997B" },
                                  { "storybookId": 2, "spineColor": "#FAC775" },
                                  { "storybookId": 3, "spineColor": "#D3C7A9" }
                                ]
                              }
                            }
                            """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "파라미터 검증 실패 (month가 1~12 범위를 벗어남 등)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "isSuccess": false,
                              "code": "COMMON400_1",
                              "message": "잘못된 요청입니다.",
                              "result": { "month": "12 이하여야 합니다" }
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
    ApiResponse<CalendarMonthlyResDTO.MonthlyInfo> getMonthly(
            @Parameter(hidden = true) Long memberId,
            @Parameter(description = "조회 연도", example = "2025") @RequestParam int year,
            @Parameter(description = "조회 월 (1~12)", example = "5") @RequestParam @Min(1) @Max(12) int month
    );

    @Operation(
            summary = "일별 기록 조회 API",
            description = """
                    # 일별 기록 조회
                    
                    ## 요청 형식
                    - 헤더: Authorization: Bearer {JWT 토큰}
                    - 경로 변수: date (yyyy-MM-dd)
                    
                    특정 날짜에 완료한 스토리북과 기록한 장 목록을 조회합니다.
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                              "isSuccess": true,
                              "code": "CALENDAR200_2",
                              "message": "일별 기록 조회를 성공했습니다.",
                              "result": {
                                "date": "2026-06-26",
                                "storybooks": [
                                  { "storybookId": 1, "title": "오래전 당신", "storybookImageUrl": "https://image1" }
                                ],
                                "chapters": [
                                  { "storybookId": 101, "title": "오래 전 당신", "nextChapterOrder": 5 }
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
                    responseCode = "404",
                    description = "토큰의 회원이 존재하지 않음 (탈퇴 등)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            { "isSuccess": false, "code": "MEMBER404_1", "message": "존재하지 않는 회원입니다.", "result": null }
                            """))
            )


    })
    ApiResponse<CalendarDailyResDTO.DailyInfo> getDaily(
            @Parameter(hidden = true) Long memberId,
            @Parameter(description = "조회할 날짜 (yyyy-MM-dd)", example = "2026-06-26") LocalDate date
    );
}