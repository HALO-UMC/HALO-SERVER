package com.umc.halo.domain.record.converter;

import com.umc.halo.domain.record.dto.*;

public class RecordConverter {

    public static RecordResDTO.WriteChapterRecord toWriteChapterRecord(Long memberChapterId, boolean isStorybookCompleted) {
        return RecordResDTO.WriteChapterRecord.builder()
                .memberChapterId(memberChapterId)
                .isStorybookCompleted(isStorybookCompleted)
                .build();
    }
}