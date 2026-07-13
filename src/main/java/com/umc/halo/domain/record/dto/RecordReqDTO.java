package com.umc.halo.domain.record.dto;

import com.umc.halo.domain.record.enums.*;
import com.umc.halo.global.enums.*;

import java.util.*;

public class RecordReqDTO {

    // 장 기록 작성
    public record WriteChapterRecord(
            Long storybookChapterId,
            Emotion emotion,
            CoverType coverType,
            String imageUrl,
            String imageKey,
            Long sceneCardId,
            List<Answer> answers,
            Status status
    ) {
        public record Answer(
                Long chapterQuestionId,
                String answer
        ) {
        }
    }
}
