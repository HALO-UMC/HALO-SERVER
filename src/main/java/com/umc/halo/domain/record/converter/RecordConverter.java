package com.umc.halo.domain.record.converter;

import com.umc.halo.domain.record.dto.*;
import com.umc.halo.domain.record.entity.*;
import com.umc.halo.domain.record.enums.*;

import java.util.*;

public class RecordConverter {

    public static RecordResDTO.WriteChapterRecord toWriteChapterRecord(Long memberChapterId, boolean isStorybookCompleted) {
        return RecordResDTO.WriteChapterRecord.builder()
                .memberChapterId(memberChapterId)
                .isStorybookCompleted(isStorybookCompleted)
                .build();
    }

    public static RecordResDTO.ReadChapterRecord toReadChapterRecord(MemberChapter memberChapter, List<RecordResDTO.ReadChapterRecord.Answer> answer) {
        CoverType coverType = memberChapter.getCoverType();

        return RecordResDTO.ReadChapterRecord.builder()
                .memberChapterId(memberChapter.getId())
                .title(memberChapter.getStorybookChapter().getChapter().getTitle())
                .emotion(memberChapter.getEmotion())
                .coverType(coverType)
                .imageUrl(coverType == CoverType.IMAGE ? memberChapter.getImageUrl() : null)
                .sceneCardImageUrl(coverType == CoverType.SCENE_CARD ? memberChapter.getSceneCard().getImageUrl() : null)
                .answers(answer)
                .completedDate(memberChapter.getCompletedDate())
                .build();
    }

    public static RecordResDTO.ReadChapterRecord.Answer toAnswer(MemberChapterAnswer memberChapterAnswer) {
        return RecordResDTO.ReadChapterRecord.Answer.builder()
                .answer(memberChapterAnswer.getAnswer())
                .question(memberChapterAnswer.getChapterQuestion().getQuestion())
                .build();
    }
}