package com.umc.halo.domain.image.controller.docs;

import com.umc.halo.domain.image.dto.*;
import com.umc.halo.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.*;
import org.springframework.security.core.annotation.*;
import org.springframework.web.bind.annotation.*;

@Tag(name = "이미지 API")
public interface ImageControllerDocs {

    // presigned URL 발급
    @Operation(
            summary = "presigned URL 발급 API",
            description = """
                    # presigned URL 발급
                    이미지를 S3에 직접 업로드할 수 있는 presigned URL을 발급합니다. 실제 파일 업로드는 서버를 거치지 않고 클라이언트가 이 URL로 S3에 직접 PUT합니다.
                    
                    ## 요청 형식
                    - **Header**
                        - Content-Type: application/json
                        - Authorization: Bearer {Access Token}
                    - **Body**
                        - contentType : 업로드할 이미지의 MIME 타입 (image/png, image/jpeg, image/jpg, image/webp만 허용)
                        - fileSize : 업로드할 이미지의 파일 크기(byte, 0보다 큰 값 필수, 10MB 이하)
                    
                    ## 동작 방식
                    1. Access Token으로 현재 회원을 인증합니다.
                    2. contentType이 허용된 이미지 형식인지, fileSize가 10MB 이하인지 검증합니다.
                    3. 회원별로 구분되는 imageKey(`pending/images/{memberId}/{uuid}.{ext}`)를 생성합니다.
                    4. SSE-KMS로 암호화되도록 서명된, 5분간 유효한 presigned URL을 발급합니다.
                    5. 클라이언트는 이 presignedUrl로 S3에 직접 PUT하여 이미지를 업로드합니다.
                    
                    ## PUT 업로드 시 클라이언트가 실어야 하는 헤더
                    아래 헤더는 presigned URL 서명에 포함되어 있어, 값이 다르면 서명이 깨져 `403 SignatureDoesNotMatch`로 업로드가 거부됩니다.
                    - `Content-Type` : 위 요청에서 보낸 contentType과 동일한 값을 사용해야 합니다.
                    - `x-amz-server-side-encryption` : 응답의 `requiredHeaders` 값을 그대로 사용해야 합니다.
                    - `x-amz-server-side-encryption-aws-kms-key-id` : 응답의 `requiredHeaders` 값을 그대로 사용해야 합니다.
                    
                    `Content-Length`는 presigned URL 서명 대상이 아니므로 값이 달라도 403이 발생하지 않습니다.
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "isSuccess": true,
                                        "code": "IMAGE200_1",
                                        "message": "presigned URL을 성공적으로 발급했습니다.",
                                        "result": {
                                            "presignedUrl": "https://halo-bucket.s3.ap-northeast-2.amazonaws.com/pending/images/1/9f1c2e3a-....png?X-Amz-Algorithm=...",
                                            "imageKey": "pending/images/1/9f1c2e3a-....png",
                                            "requiredHeaders": {
                                                "x-amz-server-side-encryption": "aws:kms",
                                                "x-amz-server-side-encryption-aws-kms-key-id": "arn:aws:kms:ap-northeast-2:123456789012:key/1234abcd-56ef-78gh-90ij-klmnopqrstuv"
                                            },
                                            "expires": 300
                                        }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "지원하지 않는 파일 형식",
                                            value = """
                                                    {
                                                        "isSuccess": false,
                                                        "code": "IMAGE400_1",
                                                        "message": "지원하지 않는 파일 형식입니다.",
                                                        "result": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "contentType이 비어있음",
                                            value = """
                                                    {
                                                        "isSuccess": false,
                                                        "code": "COMMON400_1",
                                                        "message": "잘못된 요청입니다.",
                                                        "result": {
                                                            "contentType": "contentType을 입력해주세요."
                                                        }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "fileSize 미입력",
                                            value = """
                                                    {
                                                        "isSuccess": false,
                                                        "code": "COMMON400_1",
                                                        "message": "잘못된 요청입니다.",
                                                        "result": {
                                                            "fileSize": "fileSize를 입력해주세요."
                                                        }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "fileSize가 0 이하",
                                            value = """
                                                    {
                                                        "isSuccess": false,
                                                        "code": "COMMON400_1",
                                                        "message": "잘못된 요청입니다.",
                                                        "result": {
                                                            "fileSize": "fileSize는 0보다 커야 합니다."
                                                        }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "fileSize가 10MB 초과",
                                            value = """
                                                    {
                                                        "isSuccess": false,
                                                        "code": "IMAGE400_2",
                                                        "message": "이미지 용량이 제한을 초과했습니다.(최대 10MB)",
                                                        "result": null
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "accessToken 만료·유효하지 않음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "isSuccess": false,
                                        "code": "AUTH401_1",
                                        "message": "토큰이 만료되었습니다.",
                                        "result": null
                                    }
                                    """
                            )
                    )
            )
    })
    ApiResponse<ImageResDTO.CreatePresignedUrl> createPresignedUrl(
            @Parameter(hidden = true) @AuthenticationPrincipal Long memberId,
            @RequestBody ImageReqDTO.CreatePresignedUrl dto);
}