package com.umc.halo.domain.setting.controller.docs;

import com.umc.halo.domain.setting.dto.SettingResDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import com.umc.halo.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "알림 API")
public interface SettingControllerDocs {

    // 알림 설정 조회
    @Operation(
            summary = "알림 설정 조회 API",
            description = """
                    # 알림 설정 조회
                    현재 알림 설정을 조회합니다. (정기 알림/정기 알림 시간/전체 알림/오늘의 장 알림/리텐션 알림/기념일 알림)
                    
                    ## 요청 형식
                    - **Header**
                        - Content-Type: application/json
                        - Authorization: Bearer {Access Token}
                    
                    ## 동작 방식
                    1. Access Token으로 현재 회원을 인증합니다.
                    2. 회원의 알림 설정 정보를 조회합니다.
                    3. 전체 알림 활성화 여부(isAllNotificationEnabled)를 계산합니다.
                    4. 알림 설정 정보를 반환합니다.
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
                                        "code": "NOTIFICATION200_1",
                                        "message": "알림 설정을 성공적으로 조회했습니다.",
                                        "result": {
                                            "regularNotificationEnabled": true,
                                            "regularNotificationTime": "10:00",
                                            "todayChapterNotificationEnabled": true,
                                            "retentionNotificationEnabled": false,
                                            "anniversaryNotificationEnabled": true,
                                            "isAllNotificationEnabled": false
                                        }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "memberSetting이 생성되지 않음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "isSuccess": false,
                                        "code": "SETTING404_1",
                                        "message": "설정을 찾을 수 없습니다.",
                                        "result": null
                                    }
                                    """
                            )
                    )
            )
    })
    ApiResponse<SettingResDTO.NotificationSettings> getNotificationSettings(@Parameter(hidden = true) @AuthenticationPrincipal Long memberId);
}
