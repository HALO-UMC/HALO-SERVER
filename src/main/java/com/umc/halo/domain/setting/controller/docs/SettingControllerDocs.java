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

    // BGM 목록 조회
    @Operation(
            summary = "BGM 목록 조회 API",
            description = """
                    # BGM 목록 조회
                    내장으로 다운로드받은 BGM 목록을 조회합니다.
                    
                    ## 요청 형식
                    - **Header**
                        - Content-Type: application/json
                        - Authorization: Bearer {Access Token}
                    
                    ## 동작 방식
                    1. 서버에 등록된 BGM 메타데이터를 조회합니다.
                    2. 각 BGM의 제목, 파일명, 이미지 정보를 반환합니다.
                    3. 클라이언트는 fileName을 이용하여 앱에 내장된 BGM을 재생합니다.
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
                                       "code": "BGM200_3",
                                       "message": "BGM 목록 조회를 성공했습니다.",
                                       "result": {
                                         "bgms": [
                                           {
                                             "bgmId": 1,
                                             "title": "산들바람1",
                                             "audioUrl": "http://www.bgm-example1.com",
                                             "imageUrl": "http://www.img-example1.com"
                                           },
                                           {
                                             "bgmId": 2,
                                             "title": "산들바람2",
                                             "audioUrl": "http://www.bgm-example2.com",
                                             "imageUrl": "http://www.img-example2.com"
                                           },
                                           {
                                             "bgmId": 3,
                                             "title": "산들바람3",
                                             "audioUrl": "http://www.bgm-example3.com",
                                             "imageUrl": "http://www.img-example3.com"
                                           },
                                           .
                                           .
                                           .
                                         ]
                                       }
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
    ApiResponse<SettingResDTO.Bgms> getBgms();
}
