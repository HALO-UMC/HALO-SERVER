package com.umc.halo.domain.member.controller;

import com.umc.halo.domain.member.controller.docs.MemberControllerDocs;
import com.umc.halo.domain.member.dto.request.LoginRequestDTO;
import com.umc.halo.domain.member.dto.response.LoginResponseDTO;
import com.umc.halo.domain.member.exception.code.AuthSuccessCode;
import com.umc.halo.domain.member.service.MemberService;
import com.umc.halo.global.apiPayload.ApiResponse;
import com.umc.halo.global.apiPayload.code.BaseSuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController implements MemberControllerDocs {

    private final MemberService memberService;

    @PostMapping("/v1/auth/login")
    public ApiResponse<LoginResponseDTO.LoginResponse> login(
            @RequestBody @Valid LoginRequestDTO.Login dto
    ) {
        BaseSuccessCode code = AuthSuccessCode.LOGIN_SUCCESS;
        return ApiResponse.onSuccess(code, memberService.login(dto));
    }
}
