package com.umc.halo.domain.record.service;

import com.umc.halo.domain.content.chapter.entity.*;
import com.umc.halo.domain.content.chapter.exception.*;
import com.umc.halo.domain.content.chapter.exception.code.*;
import com.umc.halo.domain.content.chapter.repository.*;
import com.umc.halo.domain.content.chapter.service.*;
import com.umc.halo.domain.content.storybook.entity.*;
import com.umc.halo.domain.content.storybook.repository.*;
import com.umc.halo.domain.member.entity.*;
import com.umc.halo.domain.member.repository.*;
import com.umc.halo.domain.record.converter.*;
import com.umc.halo.domain.record.dto.*;
import com.umc.halo.domain.record.entity.*;
import com.umc.halo.domain.record.enums.*;
import com.umc.halo.domain.record.excption.*;
import com.umc.halo.domain.record.excption.code.*;
import com.umc.halo.domain.record.repository.*;
import com.umc.halo.global.apiPayload.code.*;
import com.umc.halo.global.apiPayload.exception.*;
import lombok.*;
import org.springframework.stereotype.*;

import java.time.*;
import java.util.*;

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

    public RecordResDTO.WriteChapterRecord writeChapterRecord(Long memberId, RecordReqDTO.WriteChapterRecord recordReqDTO) {

        // member 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ProjectException(GeneralErrorCode.NOT_FOUND));

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
        if (chapterService.isCompletedToday(memberStorybook)) {
            throw new RecordException(RecordErrorCode.ALREADY_COMPLETED_TODAY);
        }

        // 아직 열리지 않은 장 / 이미 완료한 장인지 검증
        chapterService.validateChapterStatus(
                member, storybook,
                storybookChapter.getChapterOrder(), memberChapter, memberStorybook);

        // CoverType 확인
        if (recordReqDTO.coverType() != null) {
            if (recordReqDTO.coverType() == CoverType.IMAGE) {
                if (recordReqDTO.sceneCardId() != null) {
                    throw new RecordException(RecordErrorCode.INCORRECT_COVER_TYPE);
                }
            } else {
                if ((recordReqDTO.imageKey() != null) || (recordReqDTO.imageUrl() != null)) {
                    throw new RecordException(RecordErrorCode.INCORRECT_COVER_TYPE);
                }
            }
        }

        // sceneCard 조회 (findById의 값이 null이면 안되므로 별도 조회)
        SceneCard sceneCard = null;

        if (recordReqDTO.sceneCardId() != null) {
            sceneCard = sceneCardRepository.findById(recordReqDTO.sceneCardId())
                    .orElseThrow(() -> new RecordException(RecordErrorCode.NOT_FOUND_SCENE_CARD));
        }

        // answer 저장 (기존 answer 삭제 후 재저장)
        memberChapterAnswerRepository.deleteAllByMemberChapter(memberChapter);

        // memberChapter null일 경우 생성

        if (recordReqDTO.answers() != null) {
            List<MemberChapterAnswer> savedMemberChapterAnswers = recordReqDTO.answers().stream()
                    .map(a -> {

                        // chapterQuestion 조회
                        ChapterQuestion chapterQuestion = chapterQuestionRepository.findById(a.chapterQuestionId())
                                .orElseThrow(() -> new ChapterException(ChapterErrorCode.NOT_FOUND_CHAPTER_QUESTION));

                        // answer 저장
                        return MemberChapterAnswer
                                .builder()
                                .memberChapter(memberChapter)
                                .chapterQuestion(chapterQuestion)
                                .answer(a.answer())
                                .build();
                    })
                    .toList();
        }

        boolean isStorybookCompleted = true;

        if (recordReqDTO.status() == Status.COMPLETED) {
            if (storybookChapter.getChapterOrder().equals(10)) {
                isStorybookCompleted = true;

                // 추후 수정(10장 완료시 ai로 answer 3개 요약)
            }

            // 이전 memberChapter 존재 여부 확인
            MemberChapter prevMemberChapter = memberChapter;
            MemberChapter savedMemberChapter = MemberChapter.builder()
                    .member(member)
                    .storybookChapter(storybookChapter)
                    .sceneCard(sceneCard)
                    .coverType(recordReqDTO.coverType())
                    .imageUrl(recordReqDTO.imageUrl())
                    .imageKey(recordReqDTO.imageKey())
                    .completedDate(LocalDate.now())
                    .summary(null) //추후 수정
                    .status(Status.COMPLETED)
                    .build();
            memberChapterRepository.save(savedMemberChapter);

            MemberChapterAnswer savedMemberChapterAnswer = MemberChapterAnswer.builder()
                    .build();
            memberChapterAnswerRepository.save(savedMemberChapterAnswer);
        } else {
            isStorybookCompleted = false;

            MemberChapter savedMemberChapter = MemberChapter.builder()
                    .member(member)
                    .storybookChapter(storybookChapter)
                    .sceneCard(sceneCard)
                    .coverType(recordReqDTO.coverType())
                    .imageUrl(recordReqDTO.imageUrl())
                    .imageKey(recordReqDTO.imageKey())
                    .status(Status.DRAFT)
                    .build();
            memberChapterRepository.save(savedMemberChapter);

            MemberChapterAnswer savedMemberChapterAnswer = MemberChapterAnswer.builder()
                    .build();
            memberChapterAnswerRepository.save(savedMemberChapterAnswer);
        }

        return RecordConverter.toWriteChapterRecord(memberChapter.getId(), false);
    }
}
