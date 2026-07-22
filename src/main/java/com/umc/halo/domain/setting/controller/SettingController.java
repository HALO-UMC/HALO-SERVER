package com.umc.halo.domain.setting.controller;

import com.umc.halo.domain.setting.controller.docs.SettingControllerDocs;
import com.umc.halo.domain.setting.dto.SettingReqDTO;
import com.umc.halo.domain.setting.dto.SettingResDTO;
import com.umc.halo.domain.setting.exception.code.SettingSuccessCode;
import com.umc.halo.domain.setting.service.SettingService;
import com.umc.halo.global.apiPayload.ApiResponse;
import com.umc.halo.global.apiPayload.code.BaseSuccessCode;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SettingController implements SettingControllerDocs {

    private final SettingService settingService;

    @GetMapping("/v1/settings/notifications")
    public ApiResponse<SettingResDTO.NotificationSettings> getNotificationSettings(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Long memberId
    ) {
        BaseSuccessCode code = SettingSuccessCode.NOTIFICATION_SETTING_GET_SUCCESS;
        return ApiResponse.onSuccess(code, settingService.getNotificationSettings(memberId));
    }

    @GetMapping("/v1/settings/bgm")
    public ApiResponse<SettingResDTO.BgmSettings> getBgmSettings(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Long memberId
    ) {
        BaseSuccessCode code = SettingSuccessCode.BGM_SETTING_GET_SUCCESS;
        return ApiResponse.onSuccess(code, settingService.getBgmSettings(memberId));
    }

    @GetMapping("/v1/bgms")
    public ApiResponse<SettingResDTO.Bgms> getBgms() {
        BaseSuccessCode code = SettingSuccessCode.BGM_LIST_GET_SUCCESS;
        return ApiResponse.onSuccess(code, settingService.getBgms());
    }

    @PutMapping("/v1/settings/notifications")
    public ApiResponse<SettingResDTO.NotificationSettings> updateNotificationSettings(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Long memberId,
            @RequestBody @Valid SettingReqDTO.UpdateNotificationSettings dto
    ) {
        BaseSuccessCode code = SettingSuccessCode.NOTIFICATION_SETTING_UPDATE_SUCCESS;
        return ApiResponse.onSuccess(code, settingService.updateNotificationSettings(memberId, dto));
    }

    @PutMapping("/v1/settings/bgm")
    public ApiResponse<SettingResDTO.BgmSettings> updateBgmSettings(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Long memberId,
            @RequestBody @Valid SettingReqDTO.UpdateBgmSettings dto
    ) {
        BaseSuccessCode code = SettingSuccessCode.BGM_SETTING_UPDATE_SUCCESS;
        return ApiResponse.onSuccess(code, settingService.updateBgmSettings(memberId, dto));
    }
}
