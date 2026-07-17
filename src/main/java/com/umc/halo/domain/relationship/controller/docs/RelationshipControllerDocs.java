package com.umc.halo.domain.relationship.controller.docs;

import com.umc.halo.domain.relationship.dto.RelationshipResDTO;
import com.umc.halo.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "관계 정보 API")
public interface RelationshipControllerDocs {

    @Operation(
            summary = "관계 정보 조회 API",
            description = """
                    # 관계 정보 조회
                    
                    ## 요청 형식
                    - 헤더: Authorization: Bearer {JWT 토큰}
                    
                    마이페이지에서 온보딩 때 선택한 관계 태그를 조회합니다.
                    온보딩 전이면 빈 목록([])과 null을 반환합니다.
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
                                      "code": "RELATIONSHIP200_1",
                                      "message": "관계 정보 조회 성공",
                                      "result": {
                                        "parentPersonalityTags": [
                                          { "tagId": 12, "title": "다정하게 표현하는 편" },
                                          { "tagId": 15, "title": "걱정이 많은 편" }
                                        ],
                                        "currentRelationState": { "tagId": 23, "title": "안부는 하지만 조금 어색한 편" },
                                        "goalRelationships": [
                                          { "tagId": 31, "title": "어색하지 않게 이야기하고 싶어요" },
                                          { "tagId": 33, "title": "부모님을 더 알고 싶어요" }
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
    ApiResponse<RelationshipResDTO.Info> getRelationshipInfo(
            @Parameter(hidden = true) Long memberId
    );
}