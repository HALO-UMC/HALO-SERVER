package com.umc.halo.domain.content.chapter.service;

import com.umc.halo.domain.content.chapter.converter.*;
import com.umc.halo.domain.content.chapter.dto.*;
import com.umc.halo.domain.content.chapter.entity.*;
import com.umc.halo.domain.content.chapter.exception.*;
import com.umc.halo.domain.content.chapter.exception.code.*;
import com.umc.halo.domain.content.chapter.repository.*;
import com.umc.halo.domain.content.storybook.entity.*;
import com.umc.halo.domain.content.storybook.enums.*;
import com.umc.halo.domain.content.storybook.repository.*;
import com.umc.halo.domain.member.entity.*;
import com.umc.halo.domain.member.repository.*;
import com.umc.halo.domain.record.entity.*;
import com.umc.halo.domain.record.repository.*;
import com.umc.halo.global.apiPayload.code.*;
import com.umc.halo.global.apiPayload.exception.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChapterService {

    private final MemberChapterRepository memberChapterRepository;
    private final StorybookChapterRepository storybookChapterRepository;
    private final MemberRepository memberRepository;
    private final MemberStorybookRepository memberStorybookRepository;
    private final MemberChapterAnswerRepository memberChapterAnswerRepository;
    private final StorybookCharacterRepository storybookCharacterRepository;
    private final ChapterQuestionRepository chapterQuestionRepository;
    private final SceneCardRepository sceneCardRepository;

    // 오늘의 장 조회
    public ChapterResDTO.TodayChapter getTodayChapter(Long memberId, Long storybookId, Integer chapterOrder) {

        // member 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ProjectException(GeneralErrorCode.NOT_FOUND)); // 추후 수정

        // storybookChapter 조회
        StorybookChapter storybookChapter = storybookChapterRepository.findByStorybookIdAndChapterOrder(storybookId, chapterOrder)
                .orElseThrow(() -> new ChapterException(ChapterErrorCode.NOT_FOUND_CHAPTER));

        Storybook storybook = storybookChapter.getStorybook();
        Chapter chapter = storybookChapter.getChapter();

        // memberStorybook 조회
        Optional<MemberStorybook> memberStorybookOpt = memberStorybookRepository.findByStorybookAndMember(storybook, member);

        // memberStorybook이 있을 경우
        memberStorybookOpt.ifPresent(ms -> {
            Integer lastChapterOrder = ms.getLastChapterOrder();
            LocalDate lastCompletedDate = ms.getLastCompletedDate();

            // 이미 완료한 장
            if (lastChapterOrder >= chapterOrder) {
                throw new ChapterException(ChapterErrorCode.COMPLETED_CHAPTER);
            }

            // 아직 열리지 않은 장
            if ((lastChapterOrder != chapterOrder - 1) || !(lastCompletedDate.isBefore(LocalDate.now()))) {
                throw new ChapterException(ChapterErrorCode.UNOPENED_CHAPTER);
            }
        });

        // memberStorybook이 없을 경우
        if (memberStorybookOpt.isEmpty() && chapterOrder != 1) {
            throw new ChapterException(ChapterErrorCode.UNOPENED_CHAPTER);
        }

        // character 조회
        StorybookCharacter originalCharacter = storybookCharacterRepository.findByStorybookAndVariant(storybook, Variant.ORIGINAL);
        StorybookCharacter imageChoiceCharacter = storybookCharacterRepository.findByStorybookAndVariant(storybook, Variant.IMAGE_CHOICE);

        // question 조회
        List<ChapterQuestion> questions = chapterQuestionRepository.findByChapter(chapter);

        // sceneCard 조회
        List<SceneCard> sceneCards = sceneCardRepository.findByChapter(chapter);

        //memberChapter 조회
        MemberChapter memberChapter = memberChapterRepository.findByMemberAndStorybookChapter(member, storybookChapter);

        //memberChapterAnswer 조회
        List<MemberChapterAnswer> answers = memberChapter == null
                ? List.of()
                : memberChapterAnswerRepository.findByMemberChapter(memberChapter);


        return ChapterConverter.toTodayChapter(
                storybookChapter,
                originalCharacter,
                imageChoiceCharacter,
                questions,
                sceneCards,
                memberChapter,
                answers
        );
    }
}
