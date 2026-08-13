package com.umc.halo.domain.record.service;

import com.umc.halo.domain.content.chapter.entity.*;
import com.umc.halo.domain.content.chapter.exception.*;
import com.umc.halo.domain.content.chapter.exception.code.*;
import com.umc.halo.domain.content.chapter.repository.*;
import com.umc.halo.domain.content.chapter.service.*;
import com.umc.halo.domain.content.storybook.entity.*;
import com.umc.halo.domain.image.service.*;
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
    private final MemberChapterRepository memberChapterRepository;
    private final SceneCardRepository sceneCardRepository;
    private final MemberChapterAnswerRepository memberChapterAnswerRepository;
    private final ChapterQuestionRepository chapterQuestionRepository;
    private final ImageService imageService;
    private final ChapterRecordWriter chapterRecordWriter;
    private final RecordValidationReader recordValidationReader;

    public RecordResDTO.WriteChapterRecord writeChapterRecord(Long memberId, RecordReqDTO.WriteChapterRecord recordReqDTO) {
        ValidatedChapterRecord validated = validate(memberId, recordReqDTO);
        return chapterRecordWriter.persist(memberId, recordReqDTO, validated);
    }

    // 조회, coverType 구조 검증, 이미지 소유권 및 실재 여부 검증(S3 HEAD)
    private ValidatedChapterRecord validate(Long memberId, RecordReqDTO.WriteChapterRecord recordReqDTO) {

        // member/chapter/memberStorybook 조회
        RecordValidationReader.LoadedRecordContext context =
                recordValidationReader.load(memberId, recordReqDTO.chapterId());
        Chapter chapter = context.chapter();
        MemberChapter memberChapter = context.memberChapter();

        // CoverType 확인
        String imageKey = null;
        String pendingImageKey = null;
        String finalImageKey = null;
        boolean reuseExistingImage = false;
        if (recordReqDTO.coverType() != null) {
            if (recordReqDTO.coverType() == CoverType.IMAGE) {
                if (recordReqDTO.sceneCardId() != null) {
                    throw new RecordException(RecordErrorCode.INCORRECT_COVER_TYPE);
                }
                if (recordReqDTO.imageKey() == null || recordReqDTO.imageKey().isBlank()) {
                    throw new RecordException(RecordErrorCode.INCORRECT_COVER_TYPE);
                }
                // 기존 기록의 imageKey와 동일하면 그대로 사용, 락이 없기에 실제 값은 persist()가 다시 읽어서 채움
                if (memberChapter != null && imageService.isSameImage(recordReqDTO.imageKey(), memberChapter.getImageKey())) {
                    reuseExistingImage = true;
                } else {
                    ImageService.FinalizedImage resolvedImage =
                            imageService.finalizeImage(memberId, recordReqDTO.imageKey());
                    if (resolvedImage.pendingKey() != null) {
                        // 존재하는 파일(pendingKey)을 저장, 실제 prefix 제거 후 저장은 커밋 후
                        imageKey = resolvedImage.pendingKey();
                        pendingImageKey = resolvedImage.pendingKey();
                        finalImageKey = resolvedImage.finalKey();
                    } else {
                        imageKey = resolvedImage.finalKey();
                    }
                }
            } else {
                if (recordReqDTO.imageKey() != null) {
                    throw new RecordException(RecordErrorCode.INCORRECT_COVER_TYPE);
                }
                if (recordReqDTO.sceneCardId() == null) {
                    throw new RecordException(RecordErrorCode.INCORRECT_COVER_TYPE);
                }
            }
        } else {
            if ((recordReqDTO.sceneCardId() != null) || (recordReqDTO.imageKey() != null)) {
                throw new RecordException(RecordErrorCode.INCORRECT_COVER_TYPE);
            }
        }

        // 같은 질문에 답변이 중복 전송되었는지 검증
        List<Long> answeredQuestionIdList = recordReqDTO.answers() == null ? List.of()
                : recordReqDTO.answers().stream()
                .map(RecordReqDTO.WriteChapterRecord.Answer::chapterQuestionId)
                .toList();
        Set<Long> answeredQuestionIds = new HashSet<>(answeredQuestionIdList);
        if (answeredQuestionIds.size() != answeredQuestionIdList.size()) {
            throw new RecordException(RecordErrorCode.DUPLICATE_ANSWER);
        }

        // 해당 장의 질문 ID (소속 검증과 완료 검증에 공통 사용, 둘 다 필요 없으면 조회 생략)
        boolean needsChapterQuestions = !answeredQuestionIds.isEmpty() || recordReqDTO.status() == Status.COMPLETED;
        Set<Long> chapterQuestionIds = needsChapterQuestions
                ? chapterQuestionRepository.findByChapter(chapter).stream()
                .map(ChapterQuestion::getId)
                .collect(Collectors.toSet())
                : Set.of();

        // 답변한 질문이 해당 장의 질문이 맞는지 검증
        if (!chapterQuestionIds.containsAll(answeredQuestionIds)) {
            Set<Long> unknownQuestionIds = answeredQuestionIds.stream()
                    .filter(id -> !chapterQuestionIds.contains(id))
                    .collect(Collectors.toSet());
            // 실재하지 않는 질문인지 검증
            if (chapterQuestionRepository.findAllById(unknownQuestionIds).size() < unknownQuestionIds.size()) {
                throw new ChapterException(ChapterErrorCode.NOT_FOUND_CHAPTER_QUESTION);
            }
            throw new ChapterException(ChapterErrorCode.UNMATCHED_CHAPTER_QUESTION);
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
            if (!answeredQuestionIds.containsAll(chapterQuestionIds)) {
                throw new RecordException(RecordErrorCode.INCOMPLETE_ANSWERS);
            }
        }


        // sceneCard 조회 (findById의 값이 null이면 안되므로 별도 조회)
        SceneCard sceneCard = null;

        if (recordReqDTO.sceneCardId() != null) {
            sceneCard = sceneCardRepository.findById(recordReqDTO.sceneCardId())
                    .orElseThrow(() -> new ChapterException(ChapterErrorCode.NOT_FOUND_SCENE_CARD));
            if (!sceneCard.getChapter().getId().equals(chapter.getId())) {
                throw new ChapterException(ChapterErrorCode.UNMATCHED_SCENE_CARD);
            }
        }

        return new ValidatedChapterRecord(
                memberChapter != null ? memberChapter.getId() : null,
                sceneCard != null ? sceneCard.getId() : null,
                imageKey,
                pendingImageKey,
                finalImageKey,
                reuseExistingImage
        );
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