package com.umc.halo.domain.notification.controller;

import com.umc.halo.domain.notification.controller.docs.AnniversaryControllerDocs;
import com.umc.halo.domain.notification.dto.AnniversaryReqDTO;
import com.umc.halo.domain.notification.dto.AnniversaryResDTO;
import com.umc.halo.domain.notification.exception.code.AnniversarySuccessCode;
import com.umc.halo.domain.notification.service.AnniversaryService;
import com.umc.halo.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/anniversary")
@RequiredArgsConstructor
public class AnniversaryController implements AnniversaryControllerDocs {

    private final AnniversaryService anniversaryService;

    @GetMapping
    public ApiResponse<AnniversaryResDTO.GetAnniversaries> getAnniversaries(
            @AuthenticationPrincipal Long memberId
    ) {
        return ApiResponse.onSuccess(
                AnniversarySuccessCode.ANNIVERSARY_LIST_SUCCESS,
                anniversaryService.getAnniversaries(memberId)
        );
    }

    @PostMapping
    public ApiResponse<AnniversaryResDTO.CreateAnniversary> createAnniversary(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody AnniversaryReqDTO.Create request
    ) {
        return ApiResponse.onSuccess(
                AnniversarySuccessCode.ANNIVERSARY_CREATE_SUCCESS,
                anniversaryService.createAnniversary(memberId, request)
        );
    }

    @PatchMapping("/{anniversaryId}")
    public ApiResponse<AnniversaryResDTO.UpdateAnniversary> updateAnniversary(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long anniversaryId,
            @Valid @RequestBody AnniversaryReqDTO.Update request
    ) {
        return ApiResponse.onSuccess(
                AnniversarySuccessCode.ANNIVERSARY_UPDATE_SUCCESS,
                anniversaryService.updateAnniversary(memberId, anniversaryId, request)
        );
    }

    @DeleteMapping
    public ApiResponse<Void> deleteAnniversaries(
            @AuthenticationPrincipal Long memberId,
            @RequestParam @NotEmpty(message = "삭제할 기념일 ID 목록은 필수입니다.") List<Long> anniversaryIds
    ) {
        anniversaryService.deleteAnniversaries(memberId, anniversaryIds);
        return ApiResponse.onSuccess(AnniversarySuccessCode.ANNIVERSARY_DELETE_SUCCESS);
    }
}
