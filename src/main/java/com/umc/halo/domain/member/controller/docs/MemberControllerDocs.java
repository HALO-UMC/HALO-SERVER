package com.umc.halo.domain.member.controller.docs;

import com.umc.halo.domain.member.dto.MemberReqDTO;
import com.umc.halo.domain.member.dto.MemberResDTO;
import com.umc.halo.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "회원 API")
public interface MemberControllerDocs {

    // 소셜 로그인
    @Operation(
            summary = "소셜 로그인 API",
            description = """
                    # 소셜 로그인
                    카카오/구글 ID Token(OIDC)을 검증해 회원을 확인하고, 신규 회원이면 계정을 생성한 뒤 서버 토큰(access·refresh)을 발급합니다.
                    
                    ## 요청 형식
                    - **Header**
                        - Content-Type: application/json
                    - **Body**
                        - provider : 소셜 로그인 제공자 ('KAKAO' / 'GOOGLE')
                        - providerToken : Google 또는 Kakao에서 발급한 ID Token(OIDC)
                    
                    ## 동작 방식
                    1. providerToken을 검증합니다.
                    2. provider와 providerId로 기존 회원을 조회합니다.
                    3. 기존 회원이면 로그인을 진행합니다.
                    4. 회원이 없으면 신규 회원을 생성한 후 로그인합니다.
                    5. Access Token, Refresh Token, onboardingCompleted, termsAgreed를 반환합니다.
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
                                        "code": "AUTH200_1",
                                        "message": "로그인 성공",
                                        "result": {
                                            "accessToken": "eyJ...",
                                            "refreshToken": "eyJ...",
                                            "isNewUser": true,
                                            "onboardingCompleted": false,
                                            "termsAgreed": false
                                        }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청값 누락 또는 지원하지 않는 provider",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "provider 누락",
                                            value = """
                                                    {
                                                        "isSuccess": false,
                                                        "code": "COMMON400_1",
                                                        "message": "잘못된 요청입니다.",
                                                        "result": {
                                                            "provider": "provider는 필수입니다."
                                                        }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "providerToken 누락",
                                            value = """
                                                    {
                                                        "isSuccess": false,
                                                        "code": "COMMON400_1",
                                                        "message": "잘못된 요청입니다.",
                                                        "result": {
                                                            "providerToken": "providerToken은 필수입니다."
                                                        }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "provider가 KAKAO/GOOGLE 외 값",
                                            value = """
                                                    {
                                                        "isSuccess": false,
                                                        "code": "AUTH400_1",
                                                        "message": "지원하지 않는 소셜 로그인 제공자입니다.",
                                                        "result": null
                                                    }
                                                    """
                                    )
                            }
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

    // 토큰 재발급 API
    @Operation(
            summary = "토큰 재발급 API",
            description = """
                    # 토큰 재발급
                    refreshToken을 검증해 새 accessToken을 재발급합니다. 자동 로그인에도 사용.
                    
                    ## 요청 형식
                    - **Header**
                        - Content-Type: application/json
                    - **Body**
                        - refreshToken : 재발급에 사용할 Refresh Token
                    
                    ## 동작 방식
                    1. Refresh Token의 서명, 만료 여부 및 토큰 타입을 검증합니다.
                    2. 회원 DB에 저장된 Refresh Token과 일치하는지 검증합니다.
                    3. 검증에 성공하면 새로운 Access Token과 Refresh Token을 발급합니다.
                    4. onboardingCompleted와 termsAgreed 상태를 함께 반환합니다.
                    5. 기존 Refresh Token은 무효화되고 새 Refresh Token으로 교체됩니다.
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
                                        "code": "AUTH200_2",
                                        "message": "토큰 재발급 성공",
                                        "result": {
                                            "accessToken": "eyJ...",
                                            "refreshToken": "eyJ...",
                                            "onboardingCompleted": true,
                                            "termsAgreed": true
                                        }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "refreshToken 누락",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "isSuccess": false,
                                        "code": "COMMON400_1",
                                        "message": "잘못된 요청입니다.",
                                        "result": {
                                            "refreshToken": "refreshToken은 필수입니다."
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

    // 로그아웃 API
    @Operation(
            summary = "로그아웃 API",
            description = """
                    # 로그아웃
                    accessToken의 memberId로 서버가 해당 refreshToken을 찾아 무효화합니다.
                    
                    ## 요청 형식
                    - **Header**
                        - Content-Type: application/json
                        - Authorization: Bearer {Access Token}
                    
                    ## 동작 방식
                    1. Access Token으로 현재 회원을 인증합니다.
                    2. 회원 DB에 저장된 Refresh Token을 삭제합니다.
                    3. 이후 기존 Refresh Token으로는 토큰 재발급을 할 수 없습니다.
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
                                        "code": "AUTH200_3",
                                        "message": "로그아웃 성공",
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
            )
    })
    ApiResponse<Void> logout(@Parameter(hidden = true) @AuthenticationPrincipal Long memberId);

    // 회원 탈퇴 API
    @Operation(
            summary = "회원 탈퇴 API",
            description = """
                    # 회원 탈퇴
                    로그인 회원의 계정/관련 데이터를 삭제합니다.
                    
                    ## 요청 형식
                    - **Header**
                        - Content-Type: application/json
                        - Authorization: Bearer {Access Token}
                    
                    ## 동작 방식
                    1. Access Token으로 현재 회원을 인증합니다.
                    2. 회원 정보를 포함한 관련 데이터를 삭제합니다.
                    3. 회원 탈퇴가 완료되면 기존 로그인 정보는 모두 무효화됩니다.
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
                                        "code": "MEMBER200_2",
                                        "message": "회원 탈퇴가 완료되었습니다.",
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
                    description = "해당 member 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "isSuccess": false,
                                        "code": "MEMBER404_1",
                                        "message": "존재하지 않는 회원입니다.",
                                        "result": null
                                    }
                                    """
                            )
                    )
            )
    })
    ApiResponse<Void> withdraw(@Parameter(hidden = true) @AuthenticationPrincipal Long memberId);

    // 내 정보 조회
    @Operation(
            summary = "내 정보 조회 API",
            description = """
                    # 내 정보 조회
                    로그인 회원의 기본 정보를 조회합니다.
                    
                    ## 요청 형식
                    - **Header**
                        - Content-Type: application/json
                        - Authorization: Bearer {Access Token}
                    
                    # 동작 방식
                    1. Access Token으로 현재 회원을 인증합니다.
                    2. 회원 정보를 조회합니다.
                    3. 회원의 기본 정보를 반환합니다.
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
                                        "code": "MEMBER200_1",
                                        "message": "내 정보 조회 성공",
                                        "result": {
                                            "memberId": 1,
                                            "name": "신재현",
                                            "gender": "MALE",
                                            "birthDate": "2002-02-25",
                                            "provider": "KAKAO",
                                            "onboardingCompleted": true,
                                            "createdAt": "2026-06-30T14:20:00"
                                        }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "accessToken 만료",
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
                    description = "해당 member 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "isSuccess": false,
                                        "code": "MEMBER404_1",
                                        "message": "존재하지 않는 회원입니다.",
                                        "result": null
                                    }
                                    """
                            )
                    )
            )
    })
    ApiResponse<MemberResDTO.MyInfo> getMyInfo(@Parameter(hidden = true) @AuthenticationPrincipal Long memberId);
}
