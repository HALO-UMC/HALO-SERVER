package com.umc.halo.domain.content.storybook.controller;

import com.umc.halo.domain.content.storybook.dto.StorybookResDTO;
import com.umc.halo.domain.content.storybook.exception.code.HomeSuccessCode;
import com.umc.halo.domain.content.storybook.exception.code.StorybookSuccessCode;
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

    @GetMapping("/home")
    @Override
    public ApiResponse<StorybookResDTO.GetHome> getHome(
            @AuthenticationPrincipal Long memberId
    ) {
        return ApiResponse.onSuccess(
                HomeSuccessCode.GET_HOME,
                storybookService.getHome(memberId)
        );
    }

    @GetMapping("/storybooks")
    @Override
    public ApiResponse<StorybookResDTO.GetStorybookList> getStorybookList(
            @AuthenticationPrincipal Long memberId
    ) {
        return ApiResponse.onSuccess(
                StorybookSuccessCode.GET_STORYBOOK_LIST,
                storybookService.getStorybookList(memberId)
        );
    }

    @GetMapping("/storybooks/recommended")
    @Override
    public ApiResponse<StorybookResDTO.GetRecommendedStorybooks> getRecommendedStorybooks(
            @AuthenticationPrincipal Long memberId
    ) {
        return ApiResponse.onSuccess(
                StorybookSuccessCode.GET_RECOMMENDED_STORYBOOKS,
                storybookService.getRecommendedStorybooks(memberId)
        );
    }

    @GetMapping("/storybooks/{storybookId}")
    @Override
    public ApiResponse<StorybookResDTO.GetStorybookDetail> getStorybookDetail(
            @PathVariable Long storybookId,
            @AuthenticationPrincipal Long memberId
    ) {
        return ApiResponse.onSuccess(
                StorybookSuccessCode.GET_STORYBOOK_DETAIL,
                storybookService.getStorybookDetail(storybookId, memberId)
        );
    }

    @PostMapping("/storybooks/{storybookId}/start")
    @Override
    public ApiResponse<StorybookResDTO.StartStorybook> startStorybook(
            @PathVariable Long storybookId,
            @AuthenticationPrincipal Long memberId
    ) {
        return ApiResponse.onSuccess(
                StorybookSuccessCode.START_STORYBOOK,
                storybookService.startStorybook(storybookId, memberId)
        );
    }
}
