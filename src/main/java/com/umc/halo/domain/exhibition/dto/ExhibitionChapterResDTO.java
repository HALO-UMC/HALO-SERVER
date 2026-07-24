package com.umc.halo.domain.exhibition.dto;

import com.umc.halo.global.enums.Emotion;

import java.time.LocalDate;
import java.util.List;

public class ExhibitionChapterResDTO {

    public record ChaptersInfo(
            Long storybookId,
            List<ChapterInfo> chapters
    ) {}

    public record ChapterInfo(
            Integer chapterOrder,
            String chapterImageUrl,
            String title,
            String summary,
            LocalDate completedDate,
            Emotion emotion
    ) {}
}