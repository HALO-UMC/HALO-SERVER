package com.umc.halo.domain.content.storybook.controller;

import com.umc.halo.domain.content.storybook.apiPayload.StorybookSuccessCode;
import com.umc.halo.domain.content.storybook.dto.response.StorybookDetailResponse;
import com.umc.halo.domain.content.storybook.service.StorybookService;
import com.umc.halo.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StorybookController implements StorybookControllerDocs {

    private final StorybookService storybookService;

    @GetMapping("/storybooks/{storybookId}")
    @Override
    public ApiResponse<StorybookDetailResponse.GetStorybookDetail> getStorybookDetail(
            @PathVariable Long storybookId,
            @AuthenticationPrincipal Long memberId
    ) {
        return ApiResponse.onSuccess(
                StorybookSuccessCode.GET_STORYBOOK_DETAIL,
                storybookService.getStorybookDetail(storybookId, memberId)
        );
    }
}