package com.umc.halo.domain.image.converter;

import com.umc.halo.domain.image.dto.*;
import com.umc.halo.domain.image.enums.*;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.time.*;

public class ImageConverter {

    public static PutObjectRequest toPutObjectRequest(String bucket, String imageKey, ImageContentType contentType) {
        return PutObjectRequest.builder()
                .bucket(bucket)
                .key(imageKey)
                .contentType(contentType.getMimeType())
                .build();
    }

    public static PutObjectPresignRequest toPutObjectPresignRequest(Duration expiration, PutObjectRequest putObjectRequest) {
        return PutObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .putObjectRequest(putObjectRequest)
                .build();
    }

    public static ImageResDTO.CreatePresignedUrl toCreatePresignedUrl(
            PresignedPutObjectRequest presignedPutObjectRequest, String imageKey, String imageUrl, Duration expiration) {
        return ImageResDTO.CreatePresignedUrl.builder()
                .presignedUrl(presignedPutObjectRequest.url().toString())
                .imageKey(imageKey)
                .imageUrl(imageUrl)
                .expires((int) expiration.toSeconds())
                .build();
    }
}