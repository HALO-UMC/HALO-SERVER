package com.umc.halo.domain.record.service;

import com.umc.halo.domain.content.chapter.entity.*;
import com.umc.halo.domain.content.chapter.exception.*;
import com.umc.halo.domain.content.chapter.exception.code.*;
import com.umc.halo.domain.content.chapter.repository.*;
import com.umc.halo.domain.content.chapter.service.*;
import com.umc.halo.domain.content.storybook.entity.*;
import com.umc.halo.domain.content.storybook.repository.*;
import com.umc.halo.domain.image.service.*;
import com.umc.halo.domain.member.entity.*;
import com.umc.halo.domain.member.exception.*;
import com.umc.halo.domain.member.exception.code.*;
import com.umc.halo.domain.member.repository.*;
import com.umc.halo.domain.record.converter.*;
import com.umc.halo.domain.record.dto.*;
import com.umc.halo.domain.record.entity.*;
import com.umc.halo.domain.record.enums.*;
import com.umc.halo.domain.record.excption.*;
import com.umc.halo.domain.record.excption.code.*;
import com.umc.halo.domain.record.repository.*;
import com.umc.halo.global.ai.event.*;
import lombok.*;
import org.springframework.context.*;
import org.springframework.dao.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.*;
import java.util.stream.*;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final MemberRepository memberRepository;
    private final StorybookChapterRepository storybookChapterRepository;
    private final MemberChapterRepository memberChapterRepository;
    private final SceneCardRepository sceneCardRepository;
    private final MemberStorybookRepository memberStorybookRepository;
    private final MemberChapterAnswerRepository memberChapterAnswerRepository;
    private final ChapterQuestionRepository chapterQuestionRepository;
    private final ChapterService chapterService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ImageService imageService;

    @Transactional
    public RecordResDTO.WriteChapterRecord writeChapterRecord(Long memberId, RecordReqDTO.WriteChapterRecord recordReqDTO) {

        // member 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        // storybookChapter 조회
        StorybookChapter storybookChapter = storybookChapterRepository.findById(recordReqDTO.storybookChapterId())
                .orElseThrow(() -> new ChapterException(ChapterErrorCode.NOT_FOUND_CHAPTER));

        Storybook storybook = storybookChapter.getStorybook();

        // memberStorybook 조회
        MemberStorybook memberStorybook = memberStorybookRepository.findByStorybookAndMember(storybook, member)
                .orElseThrow(() -> new ChapterException(ChapterErrorCode.UNOPENED_CHAPTER));

        // memberChapter 조회
        MemberChapter memberChapter = memberChapterRepository.findByMemberAndStorybookChapter(member, storybookChapter);

        // 오늘 이미 장을 완료했는지 검증
        if (memberStorybook.isCompletedToday()) {
            throw new RecordException(RecordErrorCode.ALREADY_COMPLETED_TODAY);
        }

        // 아직 열리지 않은 장 / 이미 완료한 장인지 검증
        chapterService.validateChapterStatus(
                member, storybook,
                storybookChapter.getChapterOrder(), memberChapter, memberStorybook);

        // CoverType 확인
        String imageKey = null;
        if (recordReqDTO.coverType() != null) {
            if (recordReqDTO.coverType() == CoverType.IMAGE) {
                if (recordReqDTO.sceneCardId() != null) {
                    throw new RecordException(RecordErrorCode.INCORRECT_COVER_TYPE);
                }
                if ((recordReqDTO.imageUrl() == null) || (recordReqDTO.imageKey() == null)) {
                    throw new RecordException(RecordErrorCode.INCORRECT_COVER_TYPE);
                }
                // 기존 기록의 imageKey와 동일하면 그대로 사용
                if (memberChapter != null && recordReqDTO.imageKey().equals(memberChapter.getImageKey())) {
                    imageKey = memberChapter.getImageKey();
                } else {
                    imageKey = imageService.finalizeImage(memberId, recordReqDTO.imageKey()).finalKey();
                }
            } else {
                if ((recordReqDTO.imageKey() != null) || (recordReqDTO.imageUrl() != null)) {
                    throw new RecordException(RecordErrorCode.INCORRECT_COVER_TYPE);
                }
                if (recordReqDTO.sceneCardId() == null) {
                    throw new RecordException(RecordErrorCode.INCORRECT_COVER_TYPE);
                }
            }
        }

        // COMPLETED 상태일 경우 필수값 검증
        if (recordReqDTO.status() == Status.COMPLETED) {
            if (recordReqDTO.coverType() == null) {
                throw new RecordException(RecordErrorCode.MISSING_COVER_TYPE);
            }
            if (recordReqDTO.emotion() == null) {
                throw new RecordException(RecordErrorCode.MISSING_EMOTION);
            }

            // 장 질문 답변이 모두 채워졌는지 검증
            Set<Long> requiredQuestionIds = chapterQuestionRepository.findByChapter(storybookChapter.getChapter()).stream()
                    .map(ChapterQuestion::getId)
                    .collect(Collectors.toSet());
            Set<Long> answeredQuestionIds = recordReqDTO.answers() == null ? Set.of()
                    : recordReqDTO.answers().stream()
                    .map(RecordReqDTO.WriteChapterRecord.Answer::chapterQuestionId)
                    .collect(Collectors.toSet());
            if (!answeredQuestionIds.containsAll(requiredQuestionIds)) {
                throw new RecordException(RecordErrorCode.INCOMPLETE_ANSWERS);
            }
        }


        // sceneCard 조회 (findById의 값이 null이면 안되므로 별도 조회)
        SceneCard sceneCard = null;

        if (recordReqDTO.sceneCardId() != null) {
            sceneCard = sceneCardRepository.findById(recordReqDTO.sceneCardId())
                    .orElseThrow(() -> new ChapterException(ChapterErrorCode.NOT_FOUND_SCENE_CARD));
            if (!sceneCard.getChapter().getId().equals(storybookChapter.getChapter().getId())) {
                throw new ChapterException(ChapterErrorCode.UNMATCHED_SCENE_CARD);
            }
        }

        // MemberChapter 없으면 생성, 있으면 수정
        if (memberChapter == null) {
            try {
                memberChapter = RecordConverter.toMemberChapter(member, storybookChapter, sceneCard, recordReqDTO, imageKey);
                memberChapterRepository.save(memberChapter);
            } catch (DataIntegrityViolationException e) {
                throw new RecordException(RecordErrorCode.DUPLICATE_MEMBER_CHAPTER);
            }
        } else {
            memberChapter.updateRecord(storybookChapter, sceneCard, recordReqDTO.emotion(),
                    recordReqDTO.coverType(), imageKey, recordReqDTO.status());
        }

        final MemberChapter resolvedMemberChapter = memberChapter;

        // answer 저장 (기존 answer 삭제 후 재저장)
        memberChapterAnswerRepository.deleteAllByMemberChapter(memberChapter);


        if (recordReqDTO.answers() != null) {
            List<Long> chapterQuestionIds = recordReqDTO.answers().stream()
                    .map(RecordReqDTO.WriteChapterRecord.Answer::chapterQuestionId)
                    .toList();

            Map<Long, ChapterQuestion> chapterQuestionById = chapterQuestionRepository.findAllById(chapterQuestionIds)
                    .stream().collect(Collectors.toMap(ChapterQuestion::getId, cq -> cq));


            List<MemberChapterAnswer> savedMemberChapterAnswers = recordReqDTO.answers().stream()
                    .map(a -> {

                        // chapterQuestion 조회
                        ChapterQuestion chapterQuestion = Optional.ofNullable(chapterQuestionById.get(a.chapterQuestionId()))
                                .orElseThrow(() -> new ChapterException(ChapterErrorCode.NOT_FOUND_CHAPTER_QUESTION));
                        if (!chapterQuestion.getChapter().getId().equals(storybookChapter.getChapter().getId())) {
                            throw new ChapterException(ChapterErrorCode.UNMATCHED_CHAPTER_QUESTION);
                        }

                        // answer 저장
                        return RecordConverter.toMemberChapterAnswer(resolvedMemberChapter, chapterQuestion, a);
                    })
                    .toList();
            memberChapterAnswerRepository.saveAll(savedMemberChapterAnswers);
        }

        // 장 완료시 ai로 answer 3개 요약
        boolean isStorybookCompleted = false;
        if (recordReqDTO.status() == Status.COMPLETED) {
            // memberStorybook 업데이트
            memberStorybook.updateCompleted(storybookChapter.getChapterOrder());

            // ai로 answer 3개 요약
            applicationEventPublisher.publishEvent(new ChapterCompletedEvent(
                    memberChapter.getId(),
                    storybookChapter.getStorybook().getTitle(),
                    storybookChapter.getChapter().getTitle(),
                    storybookChapter.getChapter().getDescription(),
                    memberChapter.getEmotion().getDescription()
            ));

            if (storybookChapter.getChapterOrder().equals(10)) {
                isStorybookCompleted = true;
            }

        } else {
            memberStorybook.updateDraft(storybookChapter.getChapterOrder());
        }

        return RecordConverter.toWriteChapterRecord(memberChapter.getId(), isStorybookCompleted);
    }

    @Transactional(readOnly = true)
    public RecordResDTO.ReadChapterRecord readChapterRecord(Long memberId, Long memberChapterId) {

        // member 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        // memberChapter 조회
        MemberChapter memberChapter = memberChapterRepository.findById(memberChapterId)
                .orElseThrow(() -> new RecordException(RecordErrorCode.NOT_FOUND_MEMBER_CHAPTER));

        // 사용자가 기록한 이미지 조회
        String imageUrl = null;
        if (memberChapter.getCoverType() == CoverType.IMAGE) {
            imageUrl = imageService.getImage(memberChapter.getImageKey());
        }

        // member의 memberChapter인지 검증
        if (!memberChapter.getMember().getId().equals(member.getId())) {
            throw new RecordException(RecordErrorCode.NOT_FOUND_MEMBER_CHAPTER);
        }

        // memberChapter COMPLETED인지 검증
        if (memberChapter.getStatus() != Status.COMPLETED) {
            throw new RecordException(RecordErrorCode.NOT_COMPLETED_MEMBER_CHAPTER);
        }

        // memberChapterAnswer 조회
        List<RecordResDTO.ReadChapterRecord.Answer> answerList = memberChapterAnswerRepository.findAllByMemberChapterOrderByQuestionOrder(memberChapter)
                .stream()
                .map(RecordConverter::toAnswer)
                .toList();

        return RecordConverter.toReadChapterRecord(memberChapter, answerList, imageUrl);
    }
}
