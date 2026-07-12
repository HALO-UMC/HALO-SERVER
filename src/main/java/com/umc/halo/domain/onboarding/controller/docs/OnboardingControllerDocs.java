package com.umc.halo.domain.onboarding.controller.docs;

import com.umc.halo.domain.onboarding.dto.OnboardingResDTO;
import com.umc.halo.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "온보딩 API")
public interface OnboardingControllerDocs {

    @Operation(
            summary = "닉네임 중복 확인 API",
            description = """
                    # 닉네임 중복 확인
                    
                    ## 요청 형식
                    - 헤더: Authorize: Bearer {JWT 토큰}
                    - 쿼리 파라미터: nickname
                    
                    닉네임은 2~10자, 특수문자를 사용할 수 없습니다.
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
                                      "code": "ONBOARDING200",
                                      "message": "사용 가능한 닉네임입니다.",
                                      "result": { "isAvailable": true }
                                    }
                                    """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "닉네임 규칙 위반",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "ONBOARDING400_1",
                                      "message": "닉네임은 2~10자, 특수문자를 사용할 수 없습니다.",
                                      "result": null
                                    }
                                    """)
                    )
            )
    })
    ApiResponse<OnboardingResDTO.NicknameCheck> checkNickname(
            @Parameter(description = "확인할 닉네임", example = "정민") String nickname
    );
}