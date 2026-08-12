package com.umc.halo.domain.term.controller;

import com.umc.halo.domain.term.controller.docs.TermControllerDocs;
import com.umc.halo.domain.term.dto.TermReqDTO;
import com.umc.halo.domain.term.dto.TermResDTO;
import com.umc.halo.domain.term.exception.code.TermSuccessCode;
import com.umc.halo.domain.term.service.TermService;
import com.umc.halo.global.apiPayload.ApiResponse;
import com.umc.halo.global.apiPayload.code.BaseSuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/terms")
public class TermController implements TermControllerDocs {

    private final TermService termService;

    // 약관 목록 조회
    @Override
    @GetMapping
    public ApiResponse<List<TermResDTO.Info>> getTerms() {
        BaseSuccessCode code = TermSuccessCode.LIST_SUCCESS;
        return ApiResponse.onSuccess(code, termService.getTerms());
    }

    // 약관 동의 처리
    @Override
    @PostMapping("/agreements")
    public ApiResponse<Void> agree(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody TermReqDTO.Agree request
    ) {
        termService.agree(memberId, request);
        BaseSuccessCode code = TermSuccessCode.AGREE_SUCCESS;
        return ApiResponse.onSuccess(code);
    }

    // 약관 동의 여부 조회
    @Override
    @GetMapping("/agreements")
    public ApiResponse<TermResDTO.AgreementStatus> getAgreementStatus(
            @AuthenticationPrincipal Long memberId
    ) {
        BaseSuccessCode code = TermSuccessCode.AGREEMENT_STATUS_SUCCESS;
        return ApiResponse.onSuccess(code, termService.getAgreementStatus(memberId));
    }
}