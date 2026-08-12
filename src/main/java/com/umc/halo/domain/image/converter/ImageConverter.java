package com.umc.halo.domain.image.converter;

import com.umc.halo.domain.image.dto.*;
import com.umc.halo.domain.image.enums.*;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.time.*;
import java.util.Map;

public class ImageConverter {

    public static final String SSE_HEADER = "x-amz-server-side-encryption";
    public static final String SSE_KMS_KEY_ID_HEADER = "x-amz-server-side-encryption-aws-kms-key-id";

    public static PutObjectRequest toPutObjectRequest(String bucket, String imageKey, ImageContentType contentType, long fileSize, String kmsKeyId) {
        return PutObjectRequest.builder()
                .bucket(bucket)
                .key(imageKey)
                .contentType(contentType.getMimeType())
                .contentLength(fileSize)
                .serverSideEncryption(ServerSideEncryption.AWS_KMS)
                .ssekmsKeyId(kmsKeyId)
                .build();
    }

    public static PutObjectPresignRequest toPutObjectPresignRequest(Duration expiration, PutObjectRequest putObjectRequest) {
        return PutObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .putObjectRequest(putObjectRequest)
                .build();
    }

    public static ImageResDTO.CreatePresignedUrl toCreatePresignedUrl(
            PresignedPutObjectRequest presignedPutObjectRequest, String imageKey, Duration expiration, String kmsKeyId) {
        return ImageResDTO.CreatePresignedUrl.builder()
                .presignedUrl(presignedPutObjectRequest.url().toString())
                .imageKey(imageKey)
                .requiredHeaders(Map.of(
                        SSE_HEADER, ServerSideEncryption.AWS_KMS.toString(),
                        SSE_KMS_KEY_ID_HEADER, kmsKeyId
                ))
                .expires((int) expiration.toSeconds())
                .build();
    }

    public static CopyObjectRequest toCopyObjectRequest(String bucket, String imageKey, String finalKey, String kmsKeyId) {
        return CopyObjectRequest.builder()
                .sourceBucket(bucket)
                .sourceKey(imageKey)
                .serverSideEncryption(ServerSideEncryption.AWS_KMS)
                .ssekmsKeyId(kmsKeyId)
                .destinationBucket(bucket)
                .destinationKey(finalKey)
                .build();
    }

    public static DeleteObjectRequest toDeleteObjectRequest(String bucket, String imageKey) {
        return DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(imageKey)
                .build();
    }

    public static HeadObjectRequest toHeadObjectRequest(String bucket, String imageKey) {
        return HeadObjectRequest.builder()
                .bucket(bucket)
                .key(imageKey)
                .build();
    }

    public static GetObjectRequest toGetObjectRequest(String bucket, String imageKey) {
        return GetObjectRequest.builder()
                .bucket(bucket)
                .key(imageKey)
                .build();
    }

    public static GetObjectPresignRequest toGetObjectPresignRequest(Duration expiration, GetObjectRequest getObjectRequest) {
        return GetObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .getObjectRequest(getObjectRequest)
                .build();
    }
}
