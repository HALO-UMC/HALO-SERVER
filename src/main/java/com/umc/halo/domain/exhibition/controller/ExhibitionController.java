package com.umc.halo.domain.exhibition.controller;

import com.umc.halo.domain.exhibition.controller.docs.ExhibitionControllerDocs;
import com.umc.halo.domain.exhibition.dto.ExhibitionChapterResDTO;
import com.umc.halo.domain.exhibition.dto.ExhibitionResDTO;
import com.umc.halo.domain.exhibition.exception.code.ExhibitionSuccessCode;
import com.umc.halo.domain.exhibition.service.ExhibitionService;
import com.umc.halo.global.apiPayload.ApiResponse;
import com.umc.halo.global.apiPayload.code.BaseSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exhibitions")
public class ExhibitionController implements ExhibitionControllerDocs {

    private final ExhibitionService exhibitionService;

    @Override
    @GetMapping
    public ApiResponse<ExhibitionResDTO.MainInfo> getExhibition(
            @AuthenticationPrincipal Long memberId
    ) {
        BaseSuccessCode code = ExhibitionSuccessCode.MAIN_SUCCESS;
        return ApiResponse.onSuccess(code, exhibitionService.getExhibition(memberId));
    }

    @Override
    @GetMapping("/{storybookId}/chapters")
    public ApiResponse<ExhibitionChapterResDTO.ChaptersInfo> getChapters(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long storybookId
    ) {
        BaseSuccessCode code = ExhibitionSuccessCode.CHAPTERS_SUCCESS;
        return ApiResponse.onSuccess(code, exhibitionService.getChapters(memberId, storybookId));
    }
}