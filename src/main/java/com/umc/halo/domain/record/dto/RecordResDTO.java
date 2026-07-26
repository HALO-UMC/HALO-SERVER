package com.umc.halo.domain.record.dto;

import com.umc.halo.domain.record.enums.*;
import com.umc.halo.global.enums.*;
import lombok.*;

import java.time.*;
import java.util.*;

public class RecordResDTO {

    // 장 기록 작성
    @Builder
    public record WriteChapterRecord(
            Long memberChapterId,
            boolean isStorybookCompleted
    ) {
    }

    // 완료된 장 다시보기
    @Builder
    public record ReadChapterRecord(
            Long memberChapterId,
            String storybookTitle,
            String chapterTitle,
            Integer chapterOrder,
            String description,
            String chapterImageUrl,
            Emotion emotion,
            CoverType coverType,
            String imageUrl,
            String sceneCardImageUrl,
            List<Answer> answers,
            LocalDate completedDate
    ) {
        @Builder
        public record Answer(
                String question,
                String answer
        ) {
        }
    }

}
