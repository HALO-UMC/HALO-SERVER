package com.umc.halo.domain.image.event;

public record ImageFinalizeRequestedEvent(
        Long memberChapterId,
        String pendingKey,
        String finalKey
) {
}
