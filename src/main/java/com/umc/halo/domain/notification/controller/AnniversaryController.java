package com.umc.halo.domain.notification.controller;

import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.notification.controller.docs.AnniversaryControllerDocs;
import com.umc.halo.domain.notification.dto.AnniversaryReqDTO;
import com.umc.halo.domain.notification.dto.AnniversaryResDTO;
import com.umc.halo.domain.notification.exception.AnniversarySuccessCode;
import com.umc.halo.domain.notification.service.AnniversaryService;
import com.umc.halo.global.apiPayload.ApiResponse;
import com.umc.halo.global.security.CurrentMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/anniversary")
@RequiredArgsConstructor
public class AnniversaryController implements AnniversaryControllerDocs {

    private final AnniversaryService anniversaryService;

    @GetMapping
    public ApiResponse<AnniversaryResDTO.GetAnniversaries> getAnniversaries(
            @CurrentMember Member member
    ) {
        return ApiResponse.onSuccess(
                AnniversarySuccessCode.ANNIVERSARY_LIST_SUCCESS,
                anniversaryService.getAnniversaries(member)
        );
    }

    @PostMapping
    public ApiResponse<AnniversaryResDTO.CreateAnniversary> createAnniversary(
            @CurrentMember Member member,
            @Valid @RequestBody AnniversaryReqDTO.Create request
    ) {
        return ApiResponse.onSuccess(
                AnniversarySuccessCode.ANNIVERSARY_CREATE_SUCCESS,
                anniversaryService.createAnniversary(member, request)
        );
    }

    @PatchMapping("/{anniversaryId}")
    public ApiResponse<AnniversaryResDTO.UpdateAnniversary> updateAnniversary(
            @CurrentMember Member member,
            @PathVariable Long anniversaryId,
            @Valid @RequestBody AnniversaryReqDTO.Update request
    ) {
        return ApiResponse.onSuccess(
                AnniversarySuccessCode.ANNIVERSARY_UPDATE_SUCCESS,
                anniversaryService.updateAnniversary(member, anniversaryId, request)
        );
    }

    @DeleteMapping
    public ApiResponse<Void> deleteAnniversaries(
            @CurrentMember Member member,
            @Valid @RequestBody AnniversaryReqDTO.Delete request
    ) {
        anniversaryService.deleteAnniversaries(member, request.anniversaryIds());
        return ApiResponse.onSuccess(AnniversarySuccessCode.ANNIVERSARY_DELETE_SUCCESS);
    }
}
