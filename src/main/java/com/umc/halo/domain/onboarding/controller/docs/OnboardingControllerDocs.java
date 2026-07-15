package com.umc.halo.domain.onboarding.controller.docs;

import com.umc.halo.domain.onboarding.dto.OnboardingReqDTO;
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
                                      "code": "ONBOARDING200_3",
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

    @Operation(
            summary = "온보딩 정보 저장 API",
            description = """
                    # 온보딩 정보 저장 (step 1~5 부분 저장)
                    
                    ## 요청 형식
                    - 헤더: Authorize: Bearer {JWT 토큰}
                    - Body: step + 해당 단계 필드
                    
                    ## 단계별 필드
                    - step 1: name
                    - step 2: gender, birthDate
                    - step 3: parentPersonalityTagIds (최대 3)
                    - step 4: currentRelationStateTagId (1개)
                    - step 5: goalRelationshipTagIds (최소 1, 최대 2) → 저장 시 온보딩 완료
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "저장 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "ONBOARDING200_1",
                                      "message": "온보딩 정보가 저장되었습니다.",
                                      "result": { "onboardingStep": 1, "onboardingCompleted": false }
                                    }
                                    """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "필수값 누락 / 태그 개수 초과",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "ONBOARDING400_3",
                                      "message": "필수 입력값이 누락되었습니다.",
                                      "result": null
                                    }
                                    """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 태그",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "ONBOARDING404_1",
                                      "message": "존재하지 않는 태그입니다.",
                                      "result": null
                                    }
                                    """)
                    )
            )
    })
    ApiResponse<OnboardingResDTO.Save> saveOnboarding(
            @Parameter(hidden = true) Long memberId,
            OnboardingReqDTO.Save request
    );

    @Operation(
            summary = "온보딩 진행 상태 조회 API",
            description = """
                    # 온보딩 진행 상태 조회
                    
                    ## 요청 형식
                    - 헤더: Authorize: Bearer {JWT 토큰}
                    
                    완료 여부 + 이어할 단계(currentStep) + 지금까지 저장된 값(savedData)을 반환합니다.
                    시작 전이면 currentStep, savedData는 null 입니다.
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공 (step 3까지 진행한 예시)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "ONBOARDING200_2",
                                      "message": "온보딩 상태 조회 성공",
                                      "result": {
                                        "onboardingCompleted": false,
                                        "currentStep": 3,
                                        "savedData": {
                                          "name": "정민",
                                          "gender": "FEMALE",
                                          "birthDate": "2005-05-03",
                                          "parentPersonalityTagIds": [12, 15],
                                          "currentRelationStateTagId": null,
                                          "goalRelationshipTagIds": []
                                        }
                                      }
                                    }
                                    """)
                    )
            )
    })
    ApiResponse<OnboardingResDTO.Status> getStatus(
            @Parameter(hidden = true) Long memberId
    );
}