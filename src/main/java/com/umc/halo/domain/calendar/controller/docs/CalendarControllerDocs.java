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

                    선택한 월의 완료 페이지 수, 기록이 있는 날짜, 완성/진행 중 스토리북 통계를 조회합니다.

                    ## 요청 형식
                    - **Header**
                        - Authorization: Bearer {JWT 토큰}
                    - **Query Parameter**
                        - year : 조회 연도
                        - month : 조회 월 (1~12)

                    ## 동작 방식
                    1. 인증된 회원을 조회합니다.
                    2. 요청한 연/월(year, month)의 시작일~종료일 범위를 계산합니다.
                    3. 해당 기간에 완료(COMPLETED)된 장 기록을 조회하여 완료 페이지 수(completedPageCount)와 기록이 있는 날짜(recordedDays)를 계산합니다.
                    4. 회원의 전체 스토리북 진행 정보 중 완료(COMPLETED)한 장이 10개(전체 장 수)이고 해당 기간 내 완료된 스토리북을 완성 스토리북(completedStorybooks)으로 집계합니다.
                    5. 완료한 장이 10개 미만이고 해당 기간 내 기록이 있는 스토리북 수를 진행 중 스토리북 수(inProgressStorybookCount)로 집계합니다.
                    6. 계산된 통계와 완성 스토리북 목록을 반환합니다.
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
                                "recordedDays": [
                                  { "day": 9, "hasCompletedStorybook": false },
                                  { "day": 19, "hasCompletedStorybook": true }
                                ],
                                "completedStorybooks": [
                                  { "storybookId": 1 },
                                  { "storybookId": 2 },
                                  { "storybookId": 3 }
                                ]
                              }
                            }
                            """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "파라미터 검증 실패 (month가 1~12 범위를 벗어남 등)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "월 최솟값 범위 오류",
                                            value = """
                                                {
                                                    "isSuccess": false,
                                                    "code": "COMMON400_1",
                                                    "message": "잘못된 요청입니다.",
                                                    "result": {
                                                        "month": "1 이상이어야 합니다"
                                                    }
                                                }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "월 최댓값 범위 오류",
                                            value = """
                                                {
                                                    "isSuccess": false,
                                                    "code": "COMMON400_1",
                                                    "message": "잘못된 요청입니다.",
                                                    "result": {
                                                        "month": "12 이하여야 합니다"
                                                    }
                                                }
                                            """
                                    )
                            })
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - JWT 토큰 미삽입/만료",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                                "isSuccess": false,
                                "code": "AUTH401_1",
                                "message": "토큰이 만료되었습니다.",
                                "result": null
                            }
                            """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "토큰의 회원이 존재하지 않음 (탈퇴 등)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                                "isSuccess": false,
                                "code": "MEMBER404_1",
                                "message": "존재하지 않는 회원입니다.",
                                "result": null
                            }
                            """))
            )
    })
    ApiResponse<CalendarMonthlyResDTO.MonthlyInfo> getMonthly(
            @Parameter(hidden = true) Long memberId,
            @Parameter(description = "조회 연도", example = "2025") @RequestParam int year,
            @Parameter(description = "조회 월 (1~12)", example = "5") @RequestParam @Min(value = 1, message = "1 이상이어야 합니다.") @Max(value = 12, message = "12 이하여야 합니다.") int month
    );

    @Operation(
            summary = "일별 기록 조회 API",
            description = """
                    # 일별 기록 조회

                    특정 날짜에 완료한 스토리북과 기록한 장 목록을 조회합니다.

                    ## 요청 형식
                    - **Header**
                        - Authorization: Bearer {JWT 토큰}
                    - **Path Variable**
                        - date : 조회할 날짜 (yyyy-MM-dd)

                    ## 동작 방식
                    1. 인증된 회원을 조회합니다.
                    2. 요청한 날짜(date)에 완료(COMPLETED)된 장 기록을 최신 수정순으로 조회합니다.
                    3. 각 기록의 스토리북별 진행 상태를 계산합니다.
                    4. 완료한 장이 마지막 장(10번째)이면 완성 스토리북(storybooks) 목록에 추가합니다.
                    5. 그 외의 경우 해당 날짜에 완료한 장 순서(completedChapterOrder)를 진행 중 장(chapters) 목록에 추가합니다.
                    6. 완성 스토리북과 진행 중 장 목록을 함께 반환합니다.
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
                                  { "memberChapterId": 10, "storybookId": 101, "title": "오래 전 당신", "completedChapterOrder": 5 }
                                ]
                              }
                            }
                            """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - JWT 토큰 미삽입/만료",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                                "isSuccess": false,
                                "code": "AUTH401_1",
                                "message": "토큰이 만료되었습니다.",
                                "result": null
                            }
                            """))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "토큰의 회원이 존재하지 않음 (탈퇴 등)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {
                                "isSuccess": false,
                                "code": "MEMBER404_1",
                                "message": "존재하지 않는 회원입니다.",
                                "result": null
                            }
                            """))
            )


    })
    ApiResponse<CalendarDailyResDTO.DailyInfo> getDaily(
            @Parameter(hidden = true) Long memberId,
            @Parameter(description = "조회할 날짜 (yyyy-MM-dd)", example = "2026-06-26") LocalDate date
    );
}