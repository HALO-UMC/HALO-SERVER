package com.umc.halo.domain.member.controller;

import com.umc.halo.domain.member.dto.MemberDeviceReqDTO;
import com.umc.halo.domain.member.exception.code.MemberDeviceSuccessCode;
import com.umc.halo.domain.member.repository.MemberDeviceRepository;
import com.umc.halo.domain.member.service.MemberDeviceService;
import com.umc.halo.global.apiPayload.ApiResponse;
import com.umc.halo.global.apiPayload.code.BaseSuccessCode;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members/devices")
public class MemberDeviceController {

    private final MemberDeviceRepository memberDeviceRepository;
    private final MemberDeviceService memberDeviceService;

    @PostMapping
    public ApiResponse<Void> registerDevice(
            @Parameter(hidden = true) @AuthenticationPrincipal Long memberId,
            @RequestBody @Valid MemberDeviceReqDTO.Register dto
    ) {
        memberDeviceService.registerDevice(memberId, dto);
        BaseSuccessCode code = MemberDeviceSuccessCode.DEVICE_REGISTER_SUCCESS;
        return ApiResponse.onSuccess(code);
    }

    @DeleteMapping
    public ApiResponse<Void> deleteDevice(
            @Parameter(hidden = true) @AuthenticationPrincipal Long memberId,
            @RequestParam String deviceIdentifier
    ) {
        memberDeviceService.deleteDevice(memberId, deviceIdentifier);
        BaseSuccessCode code = MemberDeviceSuccessCode.DEVICE_DELETE_SUCCESS;
        return ApiResponse.onSuccess(code);
    }
}
