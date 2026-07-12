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
import com.umc.halo.domain.record.enums.*;
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

        // memberChapter 조회
        MemberChapter memberChapter = memberChapterRepository.findByMemberAndStorybookChapter(member, storybookChapter);

        // memberStorybook이 있을 경우
        memberStorybookOpt.ifPresent(ms -> {
            Integer lastChapterOrder = ms.getLastChapterOrder();
            LocalDate lastCompletedDate = ms.getLastCompletedDate();

            // lastCompletedDate가 오늘인 경우
            if ((lastCompletedDate != null) && (lastCompletedDate.isEqual(LocalDate.now()))) {
                throw new ChapterException(ChapterErrorCode.ALREADY_RECEIVED_TODAY);
            }

            // lastChapterOrder > chapterOrder 인 경우 이미 완료한 장
            if (lastChapterOrder > chapterOrder) {
                throw new ChapterException(ChapterErrorCode.COMPLETED_CHAPTER);
            }

            // lastChapterOrder == chapterOrder 인 경우 status가 draft이면 가능, completed이면 불가능
            else if (lastChapterOrder == chapterOrder) {

                // 이미 완료한 장 (memberChapter가 completed)
                if ((memberChapter != null) && (memberChapter.getStatus() == Status.COMPLETED)) {
                    throw new ChapterException(ChapterErrorCode.COMPLETED_CHAPTER);
                }
            }


            // lastChapterOrder == chapterOrder-1인 경우 status가 completed (&& lastCompletedDate가 이전)일 경우만 가능
            else if (lastChapterOrder == chapterOrder - 1) {

                // lastChapterOrder의 memberChapter
                StorybookChapter prevStorybookChapter = storybookChapterRepository.findByStorybookIdAndChapterOrder(storybookId, chapterOrder - 1)
                        .orElseThrow(() -> new ChapterException(ChapterErrorCode.NOT_FOUND_CHAPTER));
                MemberChapter prevMemberChapter = memberChapterRepository.findByMemberAndStorybookChapter(member, prevStorybookChapter);

                // 아직 열리지 않은 장 (lastChapterOrder의 memberChapter가 completed가 아님)
                if ((prevMemberChapter == null) || (prevMemberChapter.getStatus() == Status.DRAFT)) {
                    throw new ChapterException(ChapterErrorCode.UNOPENED_CHAPTER);
                }

            }

            // 아직 열리지 않은 장
            else if (lastChapterOrder < chapterOrder - 1) {
                throw new ChapterException(ChapterErrorCode.UNOPENED_CHAPTER);
            }
        });

        // 아직 열리지 않은 장(memberStorybook이 없으며 chapterOrder가 1이 아닐 경우)
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
