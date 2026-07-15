package com.umc.halo.domain.relationship.controller;

import com.umc.halo.domain.relationship.dto.RelationshipResDTO;
import com.umc.halo.domain.relationship.exception.code.RelationshipSuccessCode;
import com.umc.halo.domain.relationship.service.RelationshipService;
import com.umc.halo.global.apiPayload.ApiResponse;
import com.umc.halo.global.apiPayload.code.BaseSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/relationships")
public class RelationshipController {
    private final RelationshipService relationshipService;
    @GetMapping
    public ApiResponse<RelationshipResDTO.Info> getRelationshipInfo(
            @AuthenticationPrincipal Long memberId
    ) {
        BaseSuccessCode code = RelationshipSuccessCode.INFO_SUCCESS;
        return ApiResponse.onSuccess(code, relationshipService.getRelationshipInfo(memberId));
    }
}