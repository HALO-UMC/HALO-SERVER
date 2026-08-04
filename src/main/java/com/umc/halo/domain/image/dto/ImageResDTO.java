package com.umc.halo.domain.image.dto;


import lombok.*;

public class ImageResDTO {
    @Builder
    public record CreatePresignedUrl(
            String presignedUrl,
            String imageKey,
            Integer expires
    ) {
    }
}