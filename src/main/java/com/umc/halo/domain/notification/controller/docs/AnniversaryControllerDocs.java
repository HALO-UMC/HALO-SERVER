package com.umc.halo.domain.notification.controller.docs;

import com.umc.halo.domain.notification.dto.AnniversaryReqDTO;
import com.umc.halo.domain.notification.dto.AnniversaryResDTO;
import com.umc.halo.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "기념일 API")
public interface AnniversaryControllerDocs {

    @Operation(summary = "기념일 목록 조회 API",
            description = """
                    # 기념일 목록 조회

                    마이페이지 내 기념일 관리 화면에서 사용자의 기념일 정보를 조회합니다.

                    ## 요청 형식
                    - **Header**
                        - Authorization: Bearer {accessToken}

                    ## 동작 방식
                    1. 로그인한 회원이 등록한 기념일 목록을 조회합니다.
                    2. 반복 여부(isRepeated)를 기준으로 다가오는 기념일의 D-day를 계산합니다.
                    3. 음력 기반 기념일(개인 기념일 및 추석, 설날 등 기본 기념일)은 KASI(한국천문연구원) 표준 기반 음력-양력 변환 라이브러리로 실제 양력 날짜를 계산하여 D-day에 반영합니다.
                    4. 다가오는 기념일(upcomingAnniversaries), 내가 추가한 기념일(myAnniversaries), 기본 기념일(commonAnniversaries) 세 목록을 함께 반환합니다.
                    """)
    @ApiResponses(value = {
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
    ApiResponse<AnniversaryResDTO.GetAnniversaries> getAnniversaries(
            @AuthenticationPrincipal Long memberId
    );

    @Operation(summary = "기념일 추가 API",
            description = """
                    # 기념일 추가

                    사용자가 새로운 기념일을 등록합니다.

                    ## 요청 형식
                    - **Header**
                        - Content-Type: application/json
                        - Authorization: Bearer {accessToken}
                    - **Body**
                        - title : 기념일명 (필수, 20자 이하)
                        - anniversaryDate : 날짜 (필수)
                        - isLunar : 음력 여부 (필수)
                        - isRepeated : 반복 여부 (필수)
                        - sevenDaysAlarmEnabled : D-7 알림 여부 (필수)
                        - dayAlarmEnabled : 당일 알림 여부 (필수)
                        - memo : 메모 (선택, 255자 이하)

                    ## 동작 방식
                    1. 기념일명, 날짜, 알림 설정을 필수로 입력받습니다.
                    2. 메모는 선택 입력이며 255자 이하로 제한됩니다.
                    3. 요청 값을 기반으로 기념일을 저장하고, 생성된 기념일의 ID를 반환합니다.
                    """)
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "ANNIVERSARY201_1",
                                      "message": "기념일이 성공적으로 생성되었습니다.",
                                      "result": {
                                        "anniversaryId": 5
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
    ApiResponse<AnniversaryResDTO.CreateAnniversary> createAnniversary(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody AnniversaryReqDTO.Create request
    );

    @Operation(summary = "기념일 수정 API",
            description = """
                    # 기념일 수정

                    사용자가 등록한 기념일 정보를 수정합니다.

                    ## 요청 형식
                    - **Header**
                        - Content-Type: application/json
                        - Authorization: Bearer {accessToken}
                    - **Path Variable**
                        - anniversaryId : 수정할 기념일 ID
                    - **Body**
                        - title : 기념일명 (필수, 20자 이하)
                        - anniversaryDate : 날짜 (필수)
                        - isLunar : 음력 여부 (필수)
                        - isRepeated : 반복 여부 (필수)
                        - sevenDaysAlarmEnabled : D-7 알림 여부 (필수)
                        - dayAlarmEnabled : 당일 알림 여부 (필수)
                        - memo : 메모 (선택, 255자 이하)

                    ## 동작 방식
                    1. 요청한 기념일이 존재하는지 확인하고, 존재하지 않으면 404 예외를 반환합니다.
                    2. 해당 기념일이 로그인한 회원 소유인지 확인하고, 본인 소유가 아니면 403 예외를 반환합니다.
                    3. 검증을 통과하면 요청 값으로 기념일 정보를 갱신합니다.
                    """)
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
                                      "code": "ANNIVERSARY200_2",
                                      "message": "기념일이 성공적으로 수정되었습니다.",
                                      "result": {
                                        "anniversaryId": 5
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
                    description = "존재하지 않는 회원 또는 기념일",
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
                                            name = "존재하지 않는 기념일",
                                            value = """
                                                    {
                                                      "isSuccess": false,
                                                      "code": "ANNIVERSARY404_1",
                                                      "message": "존재하지 않는 기념일입니다.",
                                                      "result": null
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "본인 소유가 아닌 기념일",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "ANNIVERSARY403_1",
                                      "message": "해당 기념일에 대한 권한이 없습니다.",
                                      "result": null
                                    }
                                    """)
                    )
            )
    })
    ApiResponse<AnniversaryResDTO.UpdateAnniversary> updateAnniversary(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long anniversaryId,
            @Valid @RequestBody AnniversaryReqDTO.Update request
    );

    @Operation(summary = "기념일 삭제 API",
            description = """
                    # 기념일 삭제

                    사용자가 등록한 기념일을 하나 이상 선택하여 한 번에 삭제합니다.

                    ## 요청 형식
                    - **Header**
                        - Content-Type: application/json
                        - Authorization: Bearer {accessToken}
                    - **Body**
                        - anniversaryIds : 삭제할 기념일 ID 목록 (필수, 예: [1, 2, 5])

                    ## 동작 방식
                    1. 요청한 ID 목록이 모두 존재하는지 확인하고, 하나라도 존재하지 않으면 404 예외를 반환합니다.
                    2. 요청한 ID가 모두 로그인한 회원 소유인지 확인하고, 하나라도 본인 소유가 아니면 403 예외를 반환합니다.
                    3. 검증을 통과하면 요청한 기념일을 모두 삭제합니다. (부분 삭제는 지원하지 않으며, 검증 실패 시 전체가 실패합니다.)
                    """)
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
                                      "code": "ANNIVERSARY200_3",
                                      "message": "기념일이 성공적으로 삭제되었습니다.",
                                      "result": null
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
                    description = "존재하지 않는 회원 또는 기념일 포함",
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
                                            name = "존재하지 않는 기념일 포함",
                                            value = """
                                                    {
                                                      "isSuccess": false,
                                                      "code": "ANNIVERSARY404_1",
                                                      "message": "존재하지 않는 기념일입니다.",
                                                      "result": null
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "본인 소유가 아닌 기념일 포함",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "ANNIVERSARY403_1",
                                      "message": "해당 기념일에 대한 권한이 없습니다.",
                                      "result": null
                                    }
                                    """)
                    )
            )
    })
    ApiResponse<Void> deleteAnniversaries(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody AnniversaryReqDTO.Delete request
    );
}