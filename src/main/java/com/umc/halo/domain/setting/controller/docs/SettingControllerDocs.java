package com.umc.halo.domain.setting.controller.docs;

import com.umc.halo.domain.setting.dto.SettingReqDTO;
import com.umc.halo.domain.setting.dto.SettingResDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import com.umc.halo.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "알림 API")
public interface SettingControllerDocs {

    // 알림 설정 조회
    @Operation(
            summary = "알림 설정 조회 API",
            description = """
                    # 알림 설정 조회
                    현재 알림 설정을 조회합니다. (전체 알림 / 정기 알림 시간 / 오늘의 장 알림 / 리텐션 알림 / 기념일 알림)

                    ## 요청 형식
                    - **Header**
                        - Content-Type: application/json
                        - Authorization: Bearer {Access Token}

                    ## 동작 방식
                    1. Access Token으로 현재 회원을 인증합니다.
                    2. 회원의 알림 설정 정보를 조회합니다.
                    3. 조회한 알림 설정 정보를 반환합니다.
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
                                            "isAllNotificationEnabled": true,
                                            "regularNotificationTime": "10:00",
                                            "todayChapterNotificationEnabled": true,
                                            "retentionNotificationEnabled": false,
                                            "anniversaryNotificationEnabled": true
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

    // BGM 설정 조회
    @Operation(
            summary = "BGM 설정 조회 API",
            description = """
                    # BGM 설정 조회
                    현재 BGM 설정을 조회합니다. (선택된 배경음악, 배경음악 ON, OFF 여부/배경음악 볼륨)

                    ## 요청 형식
                    - **Header**
                        - Content-Type: application/json
                        - Authorization: Bearer {Access Token}

                    ## 동작 방식
                    1. Access Token으로 현재 회원을 인증합니다.
                    2. 회원의 BGM 설정 정보를 조회합니다.
                    3. 현재 선택된 BGM ID, BGM 활성화 여부, BGM 볼륨을 반환합니다.
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
                                        "code": "BGM200_1",
                                        "message": "BGM 설정 조회를 성공했습니다.",
                                        "result": {
                                            "bgmId": 1,
                                            "bgmEnabled": true,
                                            "bgmVolume": 70
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
    ApiResponse<SettingResDTO.BgmSettings> getBgmSettings(@Parameter(hidden = true) @AuthenticationPrincipal Long memberId);

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
                    2. 각 BGM의 제목, 파일명을 반환합니다.
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
                                             "title": "아침",
                                             "fileName": "bgm_01.mp3"
                                           },
                                           {
                                             "bgmId": 2,
                                             "title": "오후",
                                             "fileName": "bgm_02.mp3"
                                           },
                                           {
                                             "bgmId": 3,
                                             "title": "밤",
                                             "fileName": "bgm_03.mp3"
                                           }
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

    // 알림 설정 수정
    @Operation(
            summary = "알림 설정 수정 API",
            description = """
                    # 알림 설정 수정
                    알림 설정을 수정합니다. (정기 알림/정기 알림 시간/전체 알림/오늘의 장 알림/리텐션 알림/기념일 알림)

                    ## 요청 형식
                    - **Header**
                        - Content-Type: application/json
                        - Authorization: Bearer {Access Token}
                    - **Body**
                        - isAllNotificationEnabled: 전체 알림 ON/OFF 여부
                        - regularNotificationTime : 정기 알림 시간
                        - todayChapterNotificationEnabled : 오늘의 장 알림 ON/OFF 여부
                        - retentionNotificationEnabled : 리텐션 알림 ON/OFF 여부
                        - anniversaryNotificationEnabled : 기념일 알림 ON/OFF 여부

                    ## 동작 방식
                    1. Access Token으로 현재 회원을 인증합니다.
                    2. 회원의 알림 설정 정보를 조회합니다.
                    3. 요청한 알림 설정 값으로 회원의 설정을 수정합니다.
                    4. 정기 알림 활성화 여부와 관계없이 항상 regularNotificationTime으로 시간을 수정합니다.
                    5. 수정된 알림 설정 정보를 반환합니다.
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
                                        "code": "NOTIFICATION200_2",
                                        "message": "알림 설정을 성공적으로 수정했습니다.",
                                        "result": {
                                            "isAllNotificationEnabled": true,
                                            "regularNotificationTime": "10:00",
                                            "todayChapterNotificationEnabled": true,
                                            "retentionNotificationEnabled": false,
                                            "anniversaryNotificationEnabled": true
                                        }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "요청 값 누락 또는 형식 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "전체 알림 설정 여부 누락",
                                            value = """
                                                    {
                                                      "isSuccess": false,
                                                      "code": "COMMON400_1",
                                                      "message": "잘못된 요청입니다.",
                                                      "result": {
                                                        "isAllNotificationEnabled": "전체 알림 설정 여부는 필수입니다."
                                                      }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "정기 알림 시간 누락",
                                            value = """
                                                    {
                                                      "isSuccess": false,
                                                      "code": "COMMON400_1",
                                                      "message": "잘못된 요청입니다.",
                                                      "result": {
                                                        "regularNotificationTime": "정기 알림 시간은 필수입니다."
                                                      }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "오늘의 장 알림 설정 여부 누락",
                                            value = """
                                                    {
                                                      "isSuccess": false,
                                                      "code": "COMMON400_1",
                                                      "message": "잘못된 요청입니다.",
                                                      "result": {
                                                        "todayChapterNotificationEnabled": "오늘의 장 알림 설정 여부는 필수입니다."
                                                      }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "리텐션 알림 설정 여부 누락",
                                            value = """
                                                    {
                                                      "isSuccess": false,
                                                      "code": "COMMON400_1",
                                                      "message": "잘못된 요청입니다.",
                                                      "result": {
                                                        "retentionNotificationEnabled": "리텐션 알림 설정 여부는 필수입니다."
                                                      }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "기념일 알림 설정 여부 누락",
                                            value = """
                                                    {
                                                      "isSuccess": false,
                                                      "code": "COMMON400_1",
                                                      "message": "잘못된 요청입니다.",
                                                      "result": {
                                                        "anniversaryNotificationEnabled": "기념일 알림 설정 여부는 필수입니다."
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
    ApiResponse<SettingResDTO.NotificationSettings> updateNotificationSettings(@Parameter(hidden = true) @AuthenticationPrincipal Long memberId, @RequestBody @Valid SettingReqDTO.UpdateNotificationSettings dto);

    // BGM 설정 수정
    @Operation(
            summary = "BGM 설정 수정 API",
            description = """
                    # BGM 설정 수정
                    BGM 설정을 수정합니다. (선택된 배경음악, 배경음악 ON, OFF 여부/배경음악 볼륨)

                    ## 요청 형식
                    - **Header**
                        - Content-Type: application/json
                        - Authorization: Bearer {Access Token}
                    - **Body**
                        - bgmId : 선택한 BGM ID (선택하지 않은 경우 null)
                        - bgmEnabled : BGM ON/OFF 여부
                        - bgmVolume : BGM 볼륨 (0~100)

                    ## 동작 방식
                    1. Access Token으로 현재 회원을 인증합니다.
                    2. 회원의 BGM 설정 정보를 조회합니다.
                    3. bgmId가 전달된 경우 해당 BGM이 존재하는지 검증합니다.
                    4. 요청한 BGM 설정 값으로 회원의 설정을 수정합니다.
                    5. 수정된 BGM 설정 정보를 반환합니다.
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
                                        "code": "BGM200_2",
                                        "message": "BGM 설정 수정을 성공했습니다.",
                                        "result": {
                                            "bgmId": 1,
                                            "bgmEnabled": true,
                                            "bgmVolume": 70
                                        }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "bgmEnabled 필드가 null인 경우",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "isSuccess": false,
                                        "code": "BGM400_1",
                                        "message": "BGM 사용 여부는 필수입니다.",
                                        "result": null
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "bgmVolume 필드가 null인 경우",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "isSuccess": false,
                                        "code": "BGM400_2",
                                        "message": "BGM 볼륨은 필수입니다.",
                                        "result": null
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "bgmVolume 필드값이 허용 범위를 벗어난 경우",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "isSuccess": false,
                                        "code": "BGM400_3",
                                        "message": "BGM 볼륨 값이 올바르지 않습니다.",
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 bgmId가 입력된 경우",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "isSuccess": false,
                                        "code": "BGM404_1",
                                        "message": "존재하지 않는 BGM입니다.",
                                        "result": null
                                    }
                                    """
                            )
                    )
            ),
    })
    ApiResponse<SettingResDTO.BgmSettings> updateBgmSettings(@Parameter(hidden = true) @AuthenticationPrincipal Long memberId, @RequestBody @Valid SettingReqDTO.UpdateBgmSettings dto);
}