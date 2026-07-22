package com.umc.halo.domain.image.enums;

import com.umc.halo.domain.image.exception.*;
import com.umc.halo.domain.image.exception.code.*;
import lombok.*;

import java.util.*;

@Getter
@RequiredArgsConstructor
public enum ImageContentType {

    PNG("image/png", ".png"),
    JPEG("image/jpeg", ".jpg"),
    JPG("image/jpg", ".jpg"),
    WEBP("image/webp", ".webp");

    private final String mimeType;
    private final String extension;

    public static ImageContentType from(String mimeType) {
        return Arrays.stream(values())
                .filter(type -> type.getMimeType().equalsIgnoreCase(mimeType))
                .findFirst()
                .orElseThrow(() -> new ImageException(ImageErrorCode.INVALID_CONTENT_TYPE));
    }
}