package com.umc.halo.domain.image.dto;

import jakarta.validation.constraints.*;

public class ImageReqDTO {
    public record CreatePresignedUrl(
            @NotBlank(message = "contentType을 입력해주세요.")
            String contentType,
            @NotNull(message = "fileSize를 입력해주세요.")
            @Positive(message = "fileSize는 0보다 커야 합니다.")
            Long fileSize
    ) {
    }
}
