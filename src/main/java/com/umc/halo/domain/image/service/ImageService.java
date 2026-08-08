package com.umc.halo.domain.image.service;

import com.umc.halo.domain.image.converter.*;
import com.umc.halo.domain.image.dto.*;
import com.umc.halo.domain.image.enums.*;
import com.umc.halo.domain.image.exception.*;
import com.umc.halo.domain.image.exception.code.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.*;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.*;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ImageService {

    private static final Duration EXPIRATION = Duration.ofMinutes(5);
    private static final Duration READ_EXPIRATION = Duration.ofHours(1);
    public static final String PENDING_PREFIX = "pending/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    // presigned Url 발급
    public ImageResDTO.CreatePresignedUrl createPresignedUrl(
            Long memberId, ImageReqDTO.CreatePresignedUrl dto) {

        if (dto.fileSize() > MAX_FILE_SIZE) {
            throw new ImageException(ImageErrorCode.FILE_SIZE_EXCEEDED);
        }

        ImageContentType contentType = ImageContentType.from(dto.contentType());
        String imageKey = generateImageKey(memberId, contentType);

        PutObjectRequest putObjectRequest = ImageConverter.toPutObjectRequest(bucket, imageKey, contentType, dto.fileSize());
        PutObjectPresignRequest putPresignRequest = ImageConverter.toPutObjectPresignRequest(EXPIRATION, putObjectRequest);
        PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(putPresignRequest);

        return ImageConverter.toCreatePresignedUrl(presignedPutObjectRequest, imageKey, EXPIRATION);
    }

    // imageKey 발급 (presigned URL 발급시, pending/images/)
    private String generateImageKey(Long memberId, ImageContentType contentType) {
        return PENDING_PREFIX + "images/" + memberId + "/" + UUID.randomUUID() + contentType.getExtension();
    }

    // DB에 저장시
    public FinalizedImage finalizeImage(Long memberId, String imageKey) {

        if (imageKey == null) {
            return new FinalizedImage(null, null);
        }

        // 이미 finalize된 imageKey인 경우 소유권 및 실재 여부 확인
        if (!imageKey.startsWith(PENDING_PREFIX)) {
            String resolvedKey = resolveExistingImage(memberId, imageKey);
            return new FinalizedImage(resolvedKey, null);
        }

        // finalize 되지 않은 imageKey인 경우 소유권, 실재 여부, 용량 확인
        String finalKey = resolvePendingImage(memberId, imageKey);

        // 실제 이동은 트랜잭션 커밋 이후 리스너가 처리
        return new FinalizedImage(finalKey, imageKey);
    }

    // 이미 finalize된 imageKey인 경우 소유권 및 실재 여부 확인
    private String resolveExistingImage(Long memberId, String imageKey) {
        if (!imageKey.startsWith("images/" + memberId + "/")) {
            throw new ImageException(ImageErrorCode.NOT_FOUND_IN_S3);
        }

        // 실제로 업로드된 파일인지 확인
        headObjectOrThrow(imageKey);

        return imageKey;
    }

    // finalize 되지 않은 imageKey인 경우 소유권, 실재 여부, 용량 확인
    private String resolvePendingImage(Long memberId, String imageKey) {

        // 자신의 imageKey가 아닐 경우
        if (!imageKey.startsWith(PENDING_PREFIX + "images/" + memberId + "/")) {
            throw new ImageException(ImageErrorCode.NOT_FOUND_IN_S3);
        }

        // 실제로 업로드된 파일인지 확인
        HeadObjectResponse headObjectResponse = headObjectOrThrow(imageKey);


        // 실제 용량이 제한 이내인지 확인
        if (headObjectResponse.contentLength() > MAX_FILE_SIZE) {
            throw new ImageException(ImageErrorCode.FILE_SIZE_EXCEEDED);
        }
        return imageKey.substring(PENDING_PREFIX.length());
    }

    // 실제로 업로드된 파일인지 확인
    private HeadObjectResponse headObjectOrThrow(String imageKey) {
        try {
            return s3Client.headObject(ImageConverter.toHeadObjectRequest(bucket, imageKey));
        } catch (NoSuchKeyException e) {
            throw new ImageException(ImageErrorCode.NOT_FOUND_IN_S3, e);
        }
    }

    // pending/images -> images 복제
    public void copyToFinal(String imageKey, String finalKey) {
        CopyObjectRequest copyObjectRequest = ImageConverter.toCopyObjectRequest(bucket, imageKey, finalKey);
        s3Client.copyObject(copyObjectRequest);
    }

    // pending/images 삭제
    public void deleteObject(String imageKey) {
        DeleteObjectRequest deleteObjectRequest = ImageConverter.toDeleteObjectRequest(bucket, imageKey);
        s3Client.deleteObject(deleteObjectRequest);
    }

    // pending prefix 유무와 무관하게 같은 이미지인지 비교
    public boolean isSameImage(String requestImageKey, String storedImageKey) {
        if (requestImageKey == null || storedImageKey == null) {
            return false;
        }
        String normalizedKey = requestImageKey.startsWith(PENDING_PREFIX)
                ? requestImageKey.substring(PENDING_PREFIX.length())
                : requestImageKey;
        return normalizedKey.equals(storedImageKey);
    }

    // imageKey로 조회용 presigned URL 발급
    public String getImage(String imageKey) {
        GetObjectRequest getObjectRequest = ImageConverter.toGetObjectRequest(bucket, imageKey);
        GetObjectPresignRequest getObjectPresignRequest = ImageConverter.toGetObjectPresignRequest(READ_EXPIRATION, getObjectRequest);
        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);

        return presignedGetObjectRequest.url().toString();

    }

    // pendingKey가 null이 아니면 아직 실제 S3 copy/delete가 필요
    public record FinalizedImage(String finalKey, String pendingKey) {
    }
}