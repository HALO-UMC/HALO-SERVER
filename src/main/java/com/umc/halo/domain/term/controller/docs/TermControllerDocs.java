package com.umc.halo.domain.term.controller.docs;

import com.umc.halo.domain.term.dto.TermReqDTO;
import com.umc.halo.domain.term.dto.TermResDTO;
import com.umc.halo.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "약관 API")
public interface TermControllerDocs {

    @Operation(
            summary = "약관 목록 조회 API",
            description = """
                    # 약관 목록 조회

                    약관 동의 화면에 노출할 약관 목록을 조회합니다.
                    isRequired가 true면 필수 약관, false면 선택 약관입니다.

                    ## 요청 형식
                    - 인증이 필요하지 않습니다. (Public)

                    ## 동작 방식
                    1. 등록된 전체 약관을 ID 순으로 조회합니다.
                    2. 각 약관의 필수 여부(isRequired)와 함께 목록을 반환합니다.
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
                                      "code": "TERMS200_1",
                                      "message": "약관 목록 조회 성공",
                                      "result": [
                                        { "termId": 1, "title": "서비스 이용약관", "shortDescription": "HALO 서비스 이용 기준", "description": "제1조(목적) 이 약관은 HALO 서비스의 이용 조건을 규정합니다. ...", "isRequired": true, "updatedAt": "2026-07-30T21:51:59" },
                                        { "termId": 2, "title": "개인정보 처리방침", "shortDescription": "수집 항목 보관 기간 안내", "description": "회사는 이름, 생년월일 등을 수집합니다. ...", "isRequired": true, "updatedAt": "2026-07-30T21:51:59" },
                                        { "termId": 3, "title": "콘텐츠 보관 및 활용 안내", "shortDescription": "사진·기록 저장 기준", "description": "회원이 작성한 기록·사진 콘텐츠는 안전하게 저장됩니다. ...", "isRequired": true, "updatedAt": "2026-07-30T21:51:59" },
                                        { "termId": 4, "title": "마케팅 정보 수신 동의", "shortDescription": "이벤트·업데이트 안내", "isRequired": false, "description": "이벤트·프로모션 정보를 이메일 또는 푸시로 받습니다. ...", "updatedAt": "2026-07-30T21:51:59" }
                                      ]
                                    }
                                    """)
                    )
            )
    })
    ApiResponse<List<TermResDTO.Info>> getTerms();

    @Operation(
            summary = "약관 동의 처리 API",
            description = """
                    # 약관 동의 처리

                    필수 약관에 모두 동의해야 저장됩니다.
                    선택 약관은 동의하지 않아도 서비스 이용이 가능합니다.
                    이미 동의한 약관은 새로 저장하지 않고 동의 여부만 갱신합니다.

                    ## 요청 형식
                    - **Header**
                        - Authorization: Bearer {JWT 토큰}
                    - **Body**
                        - agreements : 약관별 동의 내역

                    ## 동작 방식
                    1. 인증된 회원을 조회합니다. 존재하지 않으면 404(MEMBER404_1)를 반환합니다.
                    2. 요청받은 약관 ID들이 모두 존재하는지 확인합니다. 존재하지 않으면 404(TERMS404_1)를 반환합니다.
                    3. 필수 약관(isRequired=true)이 모두 요청에 포함되어 동의(true) 처리되었는지 검증합니다. 하나라도 빠지거나 미동의면 400(TERMS400_1)을 반환합니다.
                    4. 이미 동의 이력이 있는 약관은 동의 여부만 갱신하고, 없는 약관은 새로 저장합니다. (동시 요청으로 인한 저장 충돌 시 재조회 후 갱신 처리)
                    5. 처리 결과를 반환합니다.
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
                                      "code": "TERMS200_2",
                                      "message": "약관 동의가 저장되었습니다.",
                                      "result": null
                                    }
                                    """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "필수 약관 미동의",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "TERMS400_1",
                                      "message": "필수 약관에 모두 동의해야 합니다.",
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 약관(TERMS404_1) / 토큰의 회원이 존재하지 않음(MEMBER404_1)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": false,
                                      "code": "TERMS404_1",
                                      "message": "존재하지 않는 약관입니다.",
                                      "result": null
                                    }
                                    """)
                    )
            )
    })
    ApiResponse<Void> agree(
            @Parameter(hidden = true) Long memberId,
            @Valid @RequestBody TermReqDTO.Agree request
    );

    @Operation(
            summary = "약관 동의 여부 조회 API",
            description = """
                    # 약관 동의 여부 조회

                    필수 약관 전체 동의 여부를 조회합니다.
                    앱 실행 시 약관 동의 화면 노출 여부 판단에 사용합니다.
                    필수 약관 중 하나라도 동의하지 않았으면 false를 반환합니다.

                    ## 요청 형식
                    - **Header**
                        - Authorization: Bearer {JWT 토큰}

                    ## 동작 방식
                    1. 인증된 회원을 조회합니다. 존재하지 않으면 404(MEMBER404_1)를 반환합니다.
                    2. 필수 약관 전체 개수와 회원이 동의한 필수 약관 개수를 비교합니다.
                    3. 두 개수가 같으면 termsAgreed를 true로, 다르면 false로 반환합니다.
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "isSuccess": true,
                                      "code": "TERMS200_3",
                                      "message": "약관 동의 여부 조회 성공",
                                      "result": {
                                              "termsAgreed": true,
                                              "agreements": [
                                                { "termId": 1, "isAgreed": true },
                                                { "termId": 2, "isAgreed": true },
                                                { "termId": 3, "isAgreed": true },
                                                { "termId": 4, "isAgreed": false }
                                              ]
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "토큰의 회원이 존재하지 않음 (탈퇴 등)",
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
    ApiResponse<TermResDTO.AgreementStatus> getAgreementStatus(
            @Parameter(hidden = true) Long memberId
    );

}