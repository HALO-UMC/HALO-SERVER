package com.umc.halo.domain.record.dto;

import lombok.*;

public class RecordResDTO {

    // 장 기록 작성
    @Builder
    public record WriteChapterRecord(
            Long memberChapterId,
            boolean isStorybookCompleted
    ) {
    }


}
