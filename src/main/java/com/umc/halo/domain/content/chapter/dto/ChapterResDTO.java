package com.umc.halo.domain.content.chapter.dto;

import com.umc.halo.domain.record.enums.*;
import com.umc.halo.global.enums.*;
import lombok.*;

import java.util.*;

public class ChapterResDTO {

    // 오늘의 장 조회
    @Builder
    public record TodayChapter(

            Long chapterId,
            String storybookTitle,
            String guide,
            Character character,
            List<Question> questions,
            List<SceneCard> sceneCards,
            Draft draft
    ) {
        @Builder
        public record Character(
                String writingCharacterImageUrl,
                String sceneCardCharacterImageUrl
        ) {
        }

        @Builder
        public record Question(
                Long chapterQuestionId,
                Integer questionOrder,
                String question
        ) {
        }

        @Builder
        public record SceneCard(
                Long sceneCardId,
                String imageUrl
        ) {
        }

        @Builder
        public record Draft(
                DraftStatus status,
                List<Answer> answers,
                CoverType coverType,
                String imageUrl,
                String imageKey,
                Long sceneCardId,
                Emotion emotion
        ) {
        }

        @Builder
        public record Answer(
                Long chapterQuestionId,
                Integer questionOrder,
                String answer
        ) {
        }

        public enum DraftStatus {
            NONE,
            DRAFT
        }
    }
}
