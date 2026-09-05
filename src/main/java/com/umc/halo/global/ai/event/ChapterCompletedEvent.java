package com.umc.halo.global.ai.event;

public record ChapterCompletedEvent(
        Long memberId,
        Long memberChapterId,
        String storybookTitle,
        String chapterTitle,
        String chapterDescription,
        String emotion
) {}
