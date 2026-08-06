package com.umc.halo.domain.record.service;

// persist에게 넘겨줄 값들
public record ValidatedChapterRecord(
        Long memberChapterId,   // 기존 기록 없으면 null
        Long sceneCardId,       // sceneCard 안 쓰면 null
        String imageKey,
        String pendingImageKey, // 새로 확정해야 하는 pending 이미지가 아니면 null
        String finalImageKey
) {
}