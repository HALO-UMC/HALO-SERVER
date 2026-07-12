package com.umc.halo.domain.member.controller;

import com.umc.halo.domain.member.controller.docs.MemberControllerDocs;
import com.umc.halo.domain.member.dto.MemberReqDTO;
import com.umc.halo.domain.member.dto.MemberResDTO;
import com.umc.halo.domain.member.exception.code.AuthSuccessCode;
import com.umc.halo.domain.member.service.MemberService;
import com.umc.halo.global.apiPayload.ApiResponse;
import com.umc.halo.global.apiPayload.code.BaseSuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ApiResponse<MemberResDTO.Login> login(
            @RequestBody @Valid MemberReqDTO.Login dto
    ) {
        BaseSuccessCode code = AuthSuccessCode.LOGIN_SUCCESS;
        return ApiResponse.onSuccess(code, memberService.login(dto));
    }

    @PostMapping("/v1/auth/reissue")
    public ApiResponse<MemberResDTO.TokenReissue> tokenReissue(
            @RequestBody @Valid MemberReqDTO.TokenReissue dto
    ) {
        BaseSuccessCode code = AuthSuccessCode.TOKEN_REISSUE_SUCCESS;
        return ApiResponse.onSuccess(code, memberService.tokenReissue(dto));
    }

    @PostMapping("/v1/auth/logout")
    public ApiResponse<Void> logout(
            @AuthenticationPrincipal Long memberId
    ) {
        memberService.logout(memberId);
        BaseSuccessCode code = AuthSuccessCode.LOGOUT_SUCCESS;
        return ApiResponse.onSuccess(code);

    }
}
