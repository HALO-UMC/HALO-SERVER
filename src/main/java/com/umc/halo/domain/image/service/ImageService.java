package com.umc.halo.domain.image.service;

import com.umc.halo.domain.image.converter.*;
import com.umc.halo.domain.image.dto.*;
import com.umc.halo.domain.image.enums.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.*;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.*;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ImageService {

    private static final Duration EXPIRATION = Duration.ofMinutes(5);

    private final S3Presigner s3Presigner;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    public ImageResDTO.CreatePresignedUrl createPresignedUrl(
            Long memberId, ImageReqDTO.CreatePresignedUrl dto) {

        ImageContentType contentType = ImageContentType.from(dto.contentType());
        String imageKey = generateImageKey(memberId, contentType);

        PutObjectRequest putObjectRequest = ImageConverter.toPutObjectRequest(bucket, imageKey, contentType);
        PutObjectPresignRequest putPresignRequest = ImageConverter.toPutObjectPresignRequest(EXPIRATION, putObjectRequest);
        PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(putPresignRequest);

        String imageUrl = "https://%s.s3.%s.amazonaws.com/%s".formatted(bucket, region, imageKey);

        return ImageConverter.toCreatePresignedUrl(presignedPutObjectRequest, imageKey, imageUrl, EXPIRATION);
    }

    private String generateImageKey(Long memberId, ImageContentType contentType) {
        return "images/" + memberId + "/" + UUID.randomUUID() + contentType.getExtension();
    }
}