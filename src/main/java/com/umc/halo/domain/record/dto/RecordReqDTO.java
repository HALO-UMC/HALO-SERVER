package com.umc.halo.domain.record.dto;

import com.umc.halo.domain.record.enums.*;
import com.umc.halo.global.enums.*;
import jakarta.validation.*;
import jakarta.validation.constraints.*;

import java.util.*;

public class RecordReqDTO {

    // 장 기록 작성
    public record WriteChapterRecord(
            @NotNull(message = "chapterId는 필수입니다.")
            Long chapterId,
            Emotion emotion,
            CoverType coverType,
            String imageUrl,
            String imageKey,
            Long sceneCardId,
            @Valid
            @Size(max = 3, message = "답변은 최대 3개까지 작성할 수 있습니다.")
            List<Answer> answers,
            @NotNull(message = "status는 필수입니다.")
            Status status
    ) {
        public record Answer(
                @NotNull(message = "chapterQuestionId는 필수입니다.")
                Long chapterQuestionId,
                @NotBlank(message = "답변 내용은 비어 있을 수 없습니다.")
                String answer
        ) {
        }
    }
}
