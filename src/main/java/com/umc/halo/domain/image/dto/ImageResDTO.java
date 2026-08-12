package com.umc.halo.domain.image.dto;


import lombok.*;

import java.util.Map;

public class ImageResDTO {
    @Builder
    public record CreatePresignedUrl(
            String presignedUrl,
            String imageKey,
            Map<String, String> requiredHeaders,
            Integer expires
    ) {
    }
}