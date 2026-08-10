package com.umc.halo.domain.record.service;

import com.umc.halo.domain.content.chapter.entity.*;
import com.umc.halo.domain.content.chapter.exception.*;
import com.umc.halo.domain.content.chapter.exception.code.*;
import com.umc.halo.domain.content.chapter.repository.*;
import com.umc.halo.domain.content.chapter.service.ChapterService;
import com.umc.halo.domain.content.storybook.entity.*;
import com.umc.halo.domain.image.event.ImageFinalizeRequestedEvent;
import com.umc.halo.domain.image.service.ImageService;
import com.umc.halo.domain.member.entity.*;
import com.umc.halo.domain.member.exception.*;
import com.umc.halo.domain.member.exception.code.*;
import com.umc.halo.domain.member.repository.*;
import com.umc.halo.domain.record.converter.*;
import com.umc.halo.domain.record.dto.*;
import com.umc.halo.domain.record.entity.*;
import com.umc.halo.domain.record.enums.*;
import com.umc.halo.domain.record.exception.*;
import com.umc.halo.domain.record.exception.code.*;
import com.umc.halo.domain.record.repository.*;
import com.umc.halo.global.ai.event.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.*;
import org.springframework.dao.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.*;
import java.util.stream.*;

// writeChapterRecord의 실제 DB 작성, RecordService.validate()가 검증을 다 마친 뒤 호출
@Slf4j
@Component
@RequiredArgsConstructor
public class ChapterRecordWriter {

    private final MemberRepository memberRepository;
    private final ChapterRepository chapterRepository;
    private final MemberChapterRepository memberChapterRepository;
    private final SceneCardRepository sceneCardRepository;
    private final MemberStorybookRepository memberStorybookRepository;
    private final MemberChapterAnswerRepository memberChapterAnswerRepository;
    private final ChapterQuestionRepository chapterQuestionRepository;
    private final ChapterService chapterService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public RecordResDTO.WriteChapterRecord persist(
            Long memberId, RecordReqDTO.WriteChapterRecord recordReqDTO, ValidatedChapterRecord validated) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        Chapter chapter = chapterRepository.findById(recordReqDTO.chapterId())
                .orElseThrow(() -> new ChapterException(ChapterErrorCode.NOT_FOUND_CHAPTER));

        Storybook storybook = chapter.getStorybook();

        MemberStorybook memberStorybook = memberStorybookRepository.findByStorybookAndMember(storybook, member)
                .orElseThrow(() -> new ChapterException(ChapterErrorCode.UNOPENED_CHAPTER));

        MemberChapter memberChapter = validated.memberChapterId() != null
                ? memberChapterRepository.findByIdForUpdate(validated.memberChapterId())
                .orElseThrow(() -> new RecordException(RecordErrorCode.NOT_FOUND_MEMBER_CHAPTER))
                : null;

        if (memberStorybook.isCompletedToday()) {
            throw new RecordException(RecordErrorCode.ALREADY_COMPLETED_TODAY);
        }
        chapterService.validateChapterStatus(member, storybook, chapter.getChapterOrder(), memberChapter, memberStorybook);

        SceneCard sceneCard = validated.sceneCardId() != null
                ? sceneCardRepository.findById(validated.sceneCardId())
                .orElseThrow(() -> new ChapterException(ChapterErrorCode.NOT_FOUND_SCENE_CARD))
                : null;

        String imageKey = validated.imageKey();

        // MemberChapter 없으면 생성, 있으면 수정
        if (memberChapter == null) {
            try {
                memberChapter = RecordConverter.toMemberChapter(member, chapter, sceneCard, recordReqDTO, imageKey);
                memberChapterRepository.save(memberChapter);
            } catch (DataIntegrityViolationException e) {
                throw toRecordConflictException(e);
            }
        } else {
            if (validated.reuseExistingImage()) {
                imageKey = memberChapter.getImageKey();
            } else if (validated.finalImageKey() != null
                    && validated.finalImageKey().equals(memberChapter.getImageKey())) {
                log.warn("이미지 확정 경합 감지, pending 키로 되돌리지 않음. memberChapterId={}, 시도된키={}, 유지되는키={}",
                        memberChapter.getId(), imageKey, memberChapter.getImageKey());
                imageKey = memberChapter.getImageKey();
            }

            memberChapter.updateRecord(chapter, sceneCard, recordReqDTO.emotion(),
                    recordReqDTO.coverType(), imageKey, recordReqDTO.status());

            // 이 메서드 안에서 제약 위반 잡기 위해 flush
            try {
                memberChapterRepository.saveAndFlush(memberChapter);
            } catch (DataIntegrityViolationException e) {
                throw toRecordConflictException(e);
            }
        }

        final MemberChapter resolvedMemberChapter = memberChapter;

        // 이벤트 발생시켜 prefix 수정 후 저장
        if (validated.pendingImageKey() != null && imageKey.startsWith(ImageService.PENDING_PREFIX)) {
            applicationEventPublisher.publishEvent(new ImageFinalizeRequestedEvent(
                    memberChapter.getId(), validated.pendingImageKey(), validated.finalImageKey()));
        }

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
                        if (!chapterQuestion.getChapter().getId().equals(chapter.getId())) {
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
            memberStorybook.updateCompleted(chapter.getChapterOrder());

            // ai로 answer 3개 요약
            applicationEventPublisher.publishEvent(new ChapterCompletedEvent(
                    memberChapter.getId(),
                    chapter.getStorybook().getTitle(),
                    chapter.getTitle(),
                    chapter.getDescription(),
                    memberChapter.getEmotion().getDescription()
            ));

            if (chapter.getChapterOrder().equals(10)) {
                isStorybookCompleted = true;
            }

        } else {
            memberStorybook.updateDraft(chapter.getChapterOrder());
        }

        return RecordConverter.toWriteChapterRecord(memberChapter.getId(), isStorybookCompleted);
    }

    // image_key 유니크 제약 위반이면 DUPLICATE_IMAGE_KEY, 그 외는 기존 중복 기록 충돌
    private RecordException toRecordConflictException(DataIntegrityViolationException e) {
        Throwable rootCause = e.getMostSpecificCause();
        String message = rootCause != null ? rootCause.getMessage() : null;
        if (message != null && message.contains("uk_member_chapter_image_key")) {
            return new RecordException(RecordErrorCode.DUPLICATE_IMAGE_KEY);
        }
        return new RecordException(RecordErrorCode.DUPLICATE_MEMBER_CHAPTER);
    }
}