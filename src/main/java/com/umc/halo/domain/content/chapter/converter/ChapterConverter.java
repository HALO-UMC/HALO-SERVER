package com.umc.halo.domain.content.chapter.converter;

import com.umc.halo.domain.content.chapter.dto.*;
import com.umc.halo.domain.content.chapter.entity.*;
import com.umc.halo.domain.content.storybook.entity.*;
import com.umc.halo.domain.record.entity.*;

import java.util.*;

public class ChapterConverter {

    public static ChapterResDTO.TodayChapter toTodayChapter(
            StorybookCharacter originalCharacter,
            StorybookCharacter imageChoiceCharacter,
            List<ChapterQuestion> questions,
            List<SceneCard> sceneCards,
            MemberChapter memberChapter,
            List<MemberChapterAnswer> answers,
            String imageUrl) {

        Chapter chapter = memberChapter.getChapter();

        return ChapterResDTO.TodayChapter.builder()
                .chapterId(chapter.getId())
                .storybookTitle(chapter.getStorybook().getTitle())
                .guide(chapter.getGuide())

                .character(
                        ChapterResDTO.TodayChapter.Character.builder()
                                .originalUrl(originalCharacter.getImageUrl())
                                .imageChoiceUrl(imageChoiceCharacter.getImageUrl())
                                .build()
                )

                .questions(
                        questions.stream().map(ChapterConverter::toQuestion).toList()
                )

                .sceneCards(
                        sceneCards.stream().map(ChapterConverter::toSceneCard).toList()
                )

                .draft(
                        toDraft(memberChapter, answers, imageUrl)
                )
                .build();
    }

    private static ChapterResDTO.TodayChapter.Question toQuestion(ChapterQuestion question) {
        return ChapterResDTO.TodayChapter.Question.builder()
                .chapterQuestionId(question.getId())
                .questionOrder(question.getQuestionOrder())
                .question(question.getQuestion())
                .build();

    }

    private static ChapterResDTO.TodayChapter.SceneCard toSceneCard(SceneCard sceneCard) {
        return ChapterResDTO.TodayChapter.SceneCard.builder()
                .sceneCardId(sceneCard.getId())
                .imageUrl(sceneCard.getImageUrl())
                .build();
    }


    private static ChapterResDTO.TodayChapter.Draft toDraft(
            MemberChapter memberChapter,
            List<MemberChapterAnswer> answers,
            String imageUrl) {
        if (memberChapter == null) {
            return ChapterResDTO.TodayChapter.Draft.builder()
                    .status(ChapterResDTO.TodayChapter.DraftStatus.NONE)
                    .answers(List.of())
                    .build();

        }

        SceneCard sceneCard = memberChapter.getSceneCard();

        return ChapterResDTO.TodayChapter.Draft.builder()
                .status(ChapterResDTO.TodayChapter.DraftStatus.DRAFT)
                .answers(answers.stream().map(ChapterConverter::toAnswer).toList())
                .coverType(memberChapter.getCoverType())
                .imageUrl(imageUrl)
                .imageKey(memberChapter.getImageKey())
                .sceneCardId(sceneCard == null ? null : sceneCard.getId())
                .emotion(memberChapter.getEmotion())
                .build();
    }

    private static ChapterResDTO.TodayChapter.Answer toAnswer(MemberChapterAnswer answer) {
        ChapterQuestion chapterQuestion = answer.getChapterQuestion();

        return ChapterResDTO.TodayChapter.Answer.builder()
                .chapterQuestionId(chapterQuestion.getId())
                .questionOrder(chapterQuestion.getQuestionOrder())
                .answer(answer.getAnswer())
                .build();
    }
}