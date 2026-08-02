package com.umc.halo.domain.content.chapter.service;

import com.umc.halo.domain.content.chapter.converter.*;
import com.umc.halo.domain.content.chapter.dto.*;
import com.umc.halo.domain.content.chapter.entity.*;
import com.umc.halo.domain.content.chapter.exception.*;
import com.umc.halo.domain.content.chapter.exception.code.*;
import com.umc.halo.domain.content.chapter.repository.*;
import com.umc.halo.domain.content.storybook.entity.*;
import com.umc.halo.domain.content.storybook.enums.*;
import com.umc.halo.domain.content.storybook.exception.*;
import com.umc.halo.domain.content.storybook.exception.code.*;
import com.umc.halo.domain.content.storybook.repository.*;
import com.umc.halo.domain.image.service.*;
import com.umc.halo.domain.member.entity.*;
import com.umc.halo.domain.member.exception.*;
import com.umc.halo.domain.member.exception.code.*;
import com.umc.halo.domain.member.repository.*;
import com.umc.halo.domain.record.entity.*;
import com.umc.halo.domain.record.enums.*;
import com.umc.halo.domain.record.repository.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChapterService {

    private final MemberChapterRepository memberChapterRepository;
    private final ChapterRepository chapterRepository;
    private final MemberRepository memberRepository;
    private final MemberStorybookRepository memberStorybookRepository;
    private final MemberChapterAnswerRepository memberChapterAnswerRepository;
    private final StorybookCharacterVariantRepository storybookCharacterVariantRepository;
    private final ChapterQuestionRepository chapterQuestionRepository;
    private final SceneCardRepository sceneCardRepository;
    private final ImageService imageService;

    // 오늘의 장 조회
    public ChapterResDTO.TodayChapter getTodayChapter(Long memberId, Long storybookId, Integer chapterOrder) {

        // member 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        // chapter 조회
        Chapter chapter = chapterRepository.findByStorybook_IdAndChapterOrder(storybookId, chapterOrder)
                .orElseThrow(() -> new ChapterException(ChapterErrorCode.NOT_FOUND_CHAPTER));

        Storybook storybook = chapter.getStorybook();

        // memberStorybook 조회
        Optional<MemberStorybook> memberStorybookOpt = memberStorybookRepository.findByStorybookAndMember(storybook, member);

        // memberChapter 조회
        MemberChapter memberChapter = memberChapterRepository.findByMemberAndChapter(member, chapter);

        String imageUrl = null;
        // 사용자가 기록한 이미지 조회
        if (memberChapter != null && memberChapter.getImageKey() != null) {
            imageUrl = imageService.getImage(memberChapter.getImageKey());
        }

        // memberStorybook이 있을 경우, 오늘 이미 완료했는지 검증
        memberStorybookOpt.ifPresent(ms -> {
            if (ms.isCompletedToday()) {
                throw new ChapterException(ChapterErrorCode.ALREADY_RECEIVED_TODAY);
            }
        });

        // 아직 열리지 않은 장 / 이미 완료한 장인지 검증
        validateChapterStatus(member, storybook, chapterOrder, memberChapter, memberStorybookOpt.orElse(null));

        // character 조회
        StorybookCharacterVariant writingCharacterVariant = storybookCharacterVariantRepository.findByStorybookCharacter_StorybookAndVariant(storybook, Variant.WRITING)
                .orElseThrow(() -> new StorybookException(StorybookErrorCode.NOT_FOUND_CHARACTER));
        StorybookCharacterVariant sceneCardCharacterVariant = storybookCharacterVariantRepository.findByStorybookCharacter_StorybookAndVariant(storybook, Variant.SCENE_CARD)
                .orElseThrow(() -> new StorybookException(StorybookErrorCode.NOT_FOUND_CHARACTER));

        // question 조회
        List<ChapterQuestion> questions
                = chapterQuestionRepository.findByChapterOrderByQuestionOrderAsc(chapter);

        // sceneCard 조회
        List<SceneCard> sceneCards = sceneCardRepository.findByChapter(chapter);


        //memberChapterAnswer 조회
        List<MemberChapterAnswer> answers = memberChapter == null
                ? List.of()
                : memberChapterAnswerRepository.findByMemberChapter(memberChapter);


        return ChapterConverter.toTodayChapter(
                chapter,
                writingCharacterVariant,
                sceneCardCharacterVariant,
                questions,
                sceneCards,
                memberChapter,
                answers,
                imageUrl
        );
    }

    // 아직 열리지 않은 장(CHAPTER403_1) / 이미 완료한 장(CHAPTER403_2)인지 검증 (RecordService에서도 재사용)
    public void validateChapterStatus(
            Member member, Storybook storybook,
            Integer chapterOrder, MemberChapter memberChapter, MemberStorybook memberStorybook) {

        // memberStorybook이 없는 경우
        if (memberStorybook == null) {
            throw new ChapterException(ChapterErrorCode.UNOPENED_CHAPTER);
        }

        Integer lastChapterOrder = memberStorybook.getLastChapterOrder();

        // lastChapterOrder > chapterOrder 인 경우 이미 완료한 장
        if (lastChapterOrder > chapterOrder) {
            throw new ChapterException(ChapterErrorCode.COMPLETED_CHAPTER);
        }

        // lastChapterOrder == chapterOrder 인 경우 status가 draft이면 가능, completed이면 불가능
        else if (lastChapterOrder.equals(chapterOrder)) {

            // 이미 완료한 장 (memberChapter가 completed)
            if ((memberChapter != null) && (memberChapter.getStatus() == Status.COMPLETED)) {
                throw new ChapterException(ChapterErrorCode.COMPLETED_CHAPTER);
            }
        }

        // lastChapterOrder == chapterOrder-1인 경우 status가 completed일 경우만 가능
        else if (lastChapterOrder.equals(chapterOrder - 1)) {

            // lastChapterOrder의 memberChapter
            Chapter prevChapter = chapterRepository.findByStorybook_IdAndChapterOrder(storybook.getId(), chapterOrder - 1)
                    .orElseThrow(() -> new ChapterException(ChapterErrorCode.NOT_FOUND_CHAPTER));
            MemberChapter prevMemberChapter = memberChapterRepository.findByMemberAndChapter(member, prevChapter);

            // 아직 열리지 않은 장 (lastChapterOrder의 memberChapter가 completed가 아님)
            if ((prevMemberChapter == null) || (prevMemberChapter.getStatus() == Status.DRAFT)) {
                throw new ChapterException(ChapterErrorCode.UNOPENED_CHAPTER);
            }

        }

        // 아직 열리지 않은 장
        else if (lastChapterOrder < chapterOrder - 1) {
            throw new ChapterException(ChapterErrorCode.UNOPENED_CHAPTER);
        }
    }
}
