package com.umc.halo.domain.record.converter;

import com.umc.halo.domain.content.chapter.entity.*;
import com.umc.halo.domain.member.entity.*;
import com.umc.halo.domain.record.dto.*;
import com.umc.halo.domain.record.entity.*;
import com.umc.halo.domain.record.enums.*;

import java.time.*;
import java.util.*;

public class RecordConverter {

    public static RecordResDTO.WriteChapterRecord toWriteChapterRecord(Long memberChapterId, boolean isStorybookCompleted) {
        return RecordResDTO.WriteChapterRecord.builder()
                .memberChapterId(memberChapterId)
                .isStorybookCompleted(isStorybookCompleted)
                .build();
    }

    public static RecordResDTO.ReadChapterRecord toReadChapterRecord(MemberChapter memberChapter, List<RecordResDTO.ReadChapterRecord.Answer> answer, String imageUrl) {
        CoverType coverType = memberChapter.getCoverType();
        Chapter chapter = memberChapter.getChapter();

        return RecordResDTO.ReadChapterRecord.builder()
                .memberChapterId(memberChapter.getId())
                .storybookTitle(memberChapter.getChapter().getStorybook().getTitle())
                .chapterTitle(chapter.getTitle())
                .chapterOrder(memberChapter.getChapter().getChapterOrder())
                .description(chapter.getDescription())
                .chapterImageUrl(chapter.getImageUrl())
                .emotion(memberChapter.getEmotion())
                .coverType(coverType)
                .imageUrl(coverType == CoverType.IMAGE ? imageUrl : null)
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

    public static MemberChapter toMemberChapter(Member member, Chapter chapter, SceneCard sceneCard,
                                                RecordReqDTO.WriteChapterRecord recordReqDTO, String imageKey) {
        return MemberChapter.builder()
                .member(member)
                .chapter(chapter)
                .sceneCard(sceneCard)
                .coverType(recordReqDTO.coverType())
                .imageKey(imageKey)
                .status(recordReqDTO.status())
                .emotion(recordReqDTO.emotion())
                .completedDate(recordReqDTO.status() == Status.COMPLETED ? LocalDate.now() : null)
                .build();
    }

    public static MemberChapterAnswer toMemberChapterAnswer(MemberChapter resolvedMemberChapter, ChapterQuestion chapterQuestion, RecordReqDTO.WriteChapterRecord.Answer a) {
        return MemberChapterAnswer.builder()
                .memberChapter(resolvedMemberChapter)
                .chapterQuestion(chapterQuestion)
                .answer(a.answer())
                .build();
    }
}