package com.umc.halo.domain.member.controller;

import com.umc.halo.domain.member.controller.docs.MemberControllerDocs;
import com.umc.halo.domain.member.dto.MemberReqDTO;
import com.umc.halo.domain.member.dto.MemberResDTO;
import com.umc.halo.domain.member.exception.code.AuthSuccessCode;
import com.umc.halo.domain.member.exception.code.MemberSuccessCode;
import com.umc.halo.domain.member.service.MemberService;
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
public class MemberController implements MemberControllerDocs {

    private final MemberService memberService;

    @Override
    @PostMapping("/v1/auth/login")
    public ApiResponse<MemberResDTO.Login> login(
            @RequestBody @Valid MemberReqDTO.Login dto
    ) {
        BaseSuccessCode code = AuthSuccessCode.LOGIN_SUCCESS;
        return ApiResponse.onSuccess(code, memberService.login(dto));
    }

    @Override
    @PostMapping("/v1/auth/reissue")
    public ApiResponse<MemberResDTO.TokenReissue> tokenReissue(
            @RequestBody @Valid MemberReqDTO.TokenReissue dto
    ) {
        BaseSuccessCode code = AuthSuccessCode.TOKEN_REISSUE_SUCCESS;
        return ApiResponse.onSuccess(code, memberService.tokenReissue(dto));
    }

    @Override
    @PostMapping("/v1/auth/logout")
    public ApiResponse<Void> logout(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Long memberId
    ) {
        memberService.logout(memberId);
        BaseSuccessCode code = AuthSuccessCode.LOGOUT_SUCCESS;
        return ApiResponse.onSuccess(code);
    }

    @Override
    @DeleteMapping("/v1/members/me")
    public ApiResponse<Void> withdraw(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Long memberId
    ) {
        memberService.withdraw(memberId);
        BaseSuccessCode code = MemberSuccessCode.DELETE_SUCCESS;
        return ApiResponse.onSuccess(code);
    }

    @Override
    @GetMapping("/v1/members/me")
    public ApiResponse<MemberResDTO.MyInfo> getMyInfo(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Long memberId
    ) {
        BaseSuccessCode code = MemberSuccessCode.INFO_SUCCESS;
        return ApiResponse.onSuccess(code, memberService.getMyInfo(memberId));
    }

    @Override
    @PatchMapping("/v1/members/last-access-at")
    public ApiResponse<Void> updateAccess(
            @Parameter(hidden = true) @AuthenticationPrincipal Long memberId
    ) {
        memberService.updateLastAccess(memberId);
        BaseSuccessCode code = MemberSuccessCode.MEMBER_ACCESS_UPDATED_SUCCESS;
        return ApiResponse.onSuccess(code);
    }
}
