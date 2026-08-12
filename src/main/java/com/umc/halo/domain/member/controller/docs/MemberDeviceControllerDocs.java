package com.umc.halo.domain.member.controller.docs;

import com.umc.halo.domain.member.dto.MemberDeviceReqDTO;
import com.umc.halo.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "회원 디바이스 API")
public interface MemberDeviceControllerDocs {

    // 디바이스 등록
    @Operation(
            summary = "FCM 디바이스 등록 API",
            description = """
                    # FCM 디바이스 등록
                    로그인한 회원의 FCM Push 알림 수신을 위한 디바이스 정보를 등록합니다. 이미 동일한 deviceIdentifier가 존재하는 경우 기존 디바이스 정보를 업데이트합니다.
                    
                    ## 요청 형식
                    - **Header**
                        - Content-Type: application/json
                        - Authorization: Bearer {Access Token}
                    - **Body**
                        - fcmToken : Firebase Cloud Messaging Token
                        - deviceType : 디바이스 타입 (ANDROID / IOS)
                        - deviceIdentifier : 디바이스 식별자 (프론트에서 생성하는 UUID)
                    
                    ## 동작 방식
                    1. Access Token으로 현재 회원을 인증합니다.
                    2. 회원의 기존 디바이스 정보를 조회합니다.
                    3. 동일한 deviceIdentifier가 존재하면 FCM Token과 Device Type을 갱신합니다.
                    4. 존재하지 않으면 새로운 디바이스 정보를 저장합니다.
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "isSuccess": true,
                                        "code": "MEMBER_DEVICE200_1",
                                        "message": "기기가 성공적으로 등록되었습니다.",
                                        "result": null
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청값 누락",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "FCM 토큰 누락",
                                            value = """
                                                {
                                                    "isSuccess": false,
                                                    "code": "COMMON400_1",
                                                    "message": "잘못된 요청입니다.",
                                                    "result": {
                                                        "fcmToken": "fcmToken은 필수입니다."
                                                    }
                                                }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "디바이스 타입 누락",
                                            value = """
                                                {
                                                    "isSuccess": false,
                                                    "code": "COMMON400_1",
                                                    "message": "잘못된 요청입니다.",
                                                    "result": {
                                                        "deviceType": "deviceType은 필수입니다."
                                                    }
                                                }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "디바이스 식별자 누락",
                                            value = """
                                                {
                                                    "isSuccess": false,
                                                    "code": "COMMON400_1",
                                                    "message": "잘못된 요청입니다.",
                                                    "result": {
                                                        "deviceIdentifier": "deviceIdentifier는 필수입니다."
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
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "회원 없음",
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
    ApiResponse<Void> registerDevice(@Parameter(hidden = true) @AuthenticationPrincipal Long memberId, @RequestBody MemberDeviceReqDTO.Register dto);

    // 디바이스 삭제
    @Operation(
            summary = "FCM 디바이스 삭제 API",
            description = """
                    # FCM 디바이스 삭제
                    로그인한 회원의 특정 디바이스 정보를 삭제합니다.
                    
                    ## 요청 형식
                    - **Header**
                        - Content-Type: application/json
                        - Authorization: Bearer {Access Token}
                    - **Query Parameter**
                        - deviceIdentifier : 삭제할 디바이스 식별자 (클라이언트 생성 UUID)
                    
                    ## 동작 방식
                    1. Access Token으로 현재 회원을 인증합니다.
                    2. 회원의 deviceIdentifier에 해당하는 디바이스를 조회합니다.
                    3. 존재하는 경우 해당 디바이스 정보를 삭제합니다.
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "isSuccess": true,
                                        "code": "MEMBER_DEVICE200_2",
                                        "message": "기기가 성공적으로 삭제되었습니다.",
                                        "result": null
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
                            examples = @ExampleObject(value = """
                                    {
                                        "isSuccess": false,
                                        "code": "AUTH401_1",
                                        "message": "토큰이 만료되었습니다.",
                                        "result": null
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "디바이스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "isSuccess": false,
                                        "code": "MEMBER_DEVICE404_1",
                                        "message": "등록된 기기를 찾을 수 없습니다.",
                                        "result": null
                                    }
                                    """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "회원 없음",
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
    @Parameters(value = {
            @Parameter(
                    name = "deviceIdentifier",
                    required = true,
                    description = """
                            삭제할 디바이스 식별자 (클라이언트 생성 UUID)
                            """,
                    in = ParameterIn.QUERY
            )
    })
    ApiResponse<Void> deleteDevice(@Parameter(hidden = true) @AuthenticationPrincipal Long memberId, @RequestParam String deviceIdentifier);
}
