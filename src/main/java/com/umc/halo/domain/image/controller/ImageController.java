package com.umc.halo.domain.image.controller;

import com.umc.halo.domain.image.controller.docs.*;
import com.umc.halo.domain.image.dto.*;
import com.umc.halo.domain.image.exception.code.*;
import com.umc.halo.domain.image.service.*;
import com.umc.halo.global.apiPayload.*;
import jakarta.validation.*;
import lombok.*;
import org.springframework.security.core.annotation.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ImageController implements ImageControllerDocs {

    private final ImageService imageService;

    @Override
    @PostMapping("/v1/images/presigned-url")
    public ApiResponse<ImageResDTO.CreatePresignedUrl> createPresignedUrl(
            @AuthenticationPrincipal Long memberId,
            @RequestBody @Valid ImageReqDTO.CreatePresignedUrl dto) {
        ImageResDTO.CreatePresignedUrl result = imageService.createPresignedUrl(memberId, dto);
        return ApiResponse.onSuccess(ImageSuccessCode.CREATE_PRESIGNED_URL, result);
    }
}