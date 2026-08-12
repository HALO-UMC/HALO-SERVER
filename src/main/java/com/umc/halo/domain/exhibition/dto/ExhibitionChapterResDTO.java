package com.umc.halo.domain.exhibition.dto;

import com.umc.halo.global.enums.Emotion;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public class ExhibitionChapterResDTO {

    @Builder
    public record ChaptersInfo(
            Long storybookId,
            String title,
            List<ChapterInfo> chapters
    ) {}

    @Builder
    public record ChapterInfo(
            Integer chapterOrder,
            String chapterImageUrl,
            String title,
            String summary,
            LocalDate completedDate,
            Emotion emotion
    ) {}
}