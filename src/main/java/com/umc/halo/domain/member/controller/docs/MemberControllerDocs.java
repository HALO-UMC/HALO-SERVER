package com.umc.halo.domain.member.controller.docs;

import com.umc.halo.domain.member.dto.MemberReqDTO;
import com.umc.halo.domain.member.dto.MemberResDTO;
import com.umc.halo.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "회원 API")
public interface MemberControllerDocs {

    // 소셜 로그인
    @Operation(
            summary = "소셜 로그인 API"
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
                                        "code": "AUTH200_1",
                                        "message": "로그인 성공",
                                        "result": {
                                            "accessToken": "eyJ...",
                                            "refreshToken": "eyJ...",
                                            "isNewUser": true,
                                            "onboardingCompleted": true
                                        }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "provider가 KAKAO/GOOGLE 외 값",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "isSuccess": false,
                                        "code": "AUTH400_1",
                                        "message": "지원하지 않는 소셜 로그인 제공자입니다.",
                                        "result": null
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "providerToken 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "isSuccess": false,
                                        "code": "AUTH401_3",
                                        "message": "소셜 인증 토큰 검증에 실패했습니다.",
                                        "result": null
                                    }
                                    """
                            )
                    )
            )
    })
    ApiResponse<MemberResDTO.Login> login(@RequestBody MemberReqDTO.Login dto);

    @Operation(
            summary = "토큰 재발급 API"
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
                                        "code": "AUTH200_2",
                                        "message": "토큰 재발급 성공",
                                        "result": {
                                            "accessToken": "eyJ...",
                                            "refreshToken": "eyJ..."
                                        }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "refreshToken 만료·위변조·저장값 불일치",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "isSuccess": false,
                                        "code": "AUTH401_2",
                                        "message": "refreshToken이 만료되었거나 유효하지 않습니다.",
                                        "result": null
                                    }
                                    """
                            )
                    )
            )
    })
    ApiResponse<MemberResDTO.TokenReissue> tokenReissue(@RequestBody MemberReqDTO.TokenReissue dto);
}
