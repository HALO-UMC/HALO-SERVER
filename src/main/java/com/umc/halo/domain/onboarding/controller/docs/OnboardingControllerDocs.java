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

                    닉네임은 2~10자, 특수문자를 사용할 수 없습니다.

                    ## 요청 형식
                    - **Header**
                        - Authorization: Bearer {JWT 토큰}
                    - **Query Parameter**
                        - nickname : 확인할 닉네임

                    ## 동작 방식
                    1. 닉네임 형식(2~10자, 한글/영문/숫자만 허용)을 검증합니다. 형식에 맞지 않으면 400(ONBOARDING400_1)을 반환합니다.
                    2. 형식이 유효하면 이미 사용 중인 닉네임인지 확인합니다.
                    3. 사용 가능 여부(isAvailable)를 반환합니다.
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 헤더에 JWT 토큰 미삽입/만료",
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
                    - **Header**
                        - Authorization: Bearer {JWT 토큰}
                    - **Body**
                        - step : 저장할 온보딩 단계 (1~5)
                        - step 1 : name
                        - step 2 : gender, birthDate
                        - step 3 : parentPersonalityTagIds (최대 3)
                        - step 4 : currentRelationStateTagId (1개)
                        - step 5 : goalRelationshipTagIds (최소 1, 최대 2) → 저장 시 온보딩 완료

                    ## 동작 방식
                    1. 요청받은 step(1~5)에 따라 필요한 필드를 검증합니다. 필수값이 없으면 400(ONBOARDING400_3)을 반환합니다.
                    2. step 1은 이름/닉네임 형식을, step 2는 성별과 생년월일(미래 날짜 불가)을 검증합니다.
                    3. step 3(부모님 성향 태그, 최대 3개)/step 4(현재 관계 상태 태그, 1개)/step 5(목표 관계 태그, 1~2개)는 각각 해당 카테고리의 태그인지 확인하고, 존재하지 않는 태그면 404(ONBOARDING404_1)를 반환합니다.
                    4. 태그가 포함된 step은 기존 태그를 삭제하고 새로 저장합니다.
                    5. 회원의 온보딩 단계(onboardingStep)를 갱신합니다.
                    6. step이 5(마지막 단계)이면 온보딩을 완료 처리합니다.
                    7. 저장된 단계와 완료 여부를 반환합니다.
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 헤더에 JWT 토큰 미삽입/만료",
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

                    완료 여부 + 이어할 단계(currentStep) + 지금까지 저장된 값(savedData)을 반환합니다.
                    시작 전이면 currentStep, savedData는 null 입니다.

                    ## 요청 형식
                    - **Header**
                        - Authorization: Bearer {JWT 토큰}

                    ## 동작 방식
                    1. 회원의 저장된 온보딩 단계(onboardingStep)를 조회합니다.
                    2. 온보딩을 시작한 적이 없으면(onboardingStep이 null) currentStep과 savedData를 null로 반환합니다.
                    3. 시작했다면 회원의 태그를 카테고리별로 조회하여 parentTagIds, currentRelationTagId, goalTagIds로 매핑합니다.
                    4. 온보딩 완료 여부, 이어서 진행할 단계, 지금까지 저장된 값을 함께 반환합니다.
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 - 헤더에 JWT 토큰 미삽입/만료",
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
            )
    })
    ApiResponse<OnboardingResDTO.Status> getStatus(
            @Parameter(hidden = true) Long memberId
    );
}