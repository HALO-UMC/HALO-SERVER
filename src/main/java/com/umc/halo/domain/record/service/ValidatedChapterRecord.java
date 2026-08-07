package com.umc.halo.domain.record.service;

// persist에게 넘겨줄 값들
public record ValidatedChapterRecord(
        Long memberChapterId,   // 기존 기록 없으면 null
        Long sceneCardId,       // sceneCard 안 쓰면 null
        String imageKey,
        String pendingImageKey, // 새로 확정해야 하는 pending 이미지가 아니면 null
        String finalImageKey,
        // 기존 기록과 같은 이미지라 재사용하는 경우 true, 하지만 락이 없으므로 persist()에서 다시 읽음
        boolean reuseExistingImage
) {
    public ValidatedChapterRecord(Long memberChapterId, Long sceneCardId, String imageKey,
                                  String pendingImageKey, String finalImageKey) {
        this(memberChapterId, sceneCardId, imageKey, pendingImageKey, finalImageKey, false);
    }
}