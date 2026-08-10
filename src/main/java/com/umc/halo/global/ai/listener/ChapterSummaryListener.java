package com.umc.halo.global.ai.listener;

import com.umc.halo.domain.record.entity.MemberChapter;
import com.umc.halo.domain.record.repository.MemberChapterAnswerRepository;
import com.umc.halo.domain.record.repository.MemberChapterRepository;
import com.umc.halo.global.ai.QuestionAnswer;
import com.umc.halo.global.ai.event.ChapterCompletedEvent;
import com.umc.halo.global.ai.exception.AiException;
import com.umc.halo.global.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChapterSummaryListener {

    private final MemberChapterRepository memberChapterRepository;
    private final MemberChapterAnswerRepository memberChapterAnswerRepository;
    private final AiService aiService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void generateSummary(ChapterCompletedEvent event) {

        MemberChapter memberChapter = memberChapterRepository.findById(event.memberChapterId()).orElseThrow();

        List<QuestionAnswer> questionAnswers =
                memberChapterAnswerRepository
                        .findAllByMemberChapterOrderByQuestionOrder(memberChapter)
                        .stream()
                        .map(answer ->
                                new QuestionAnswer(
                                        answer.getChapterQuestion().getQuestion(),
                                        answer.getAnswer()
                                )
                        )
                        .toList();

        try {
            String summary =
                    aiService.generateChapterSummary(
                            event.storybookTitle(),
                            event.chapterTitle(),
                            event.chapterDescription(),
                            questionAnswers,
                            event.emotion()
                    );
            memberChapter.updateSummary(summary);
            memberChapterRepository.save(memberChapter);
        } catch (AiException e) {
            log.warn("AI 요약 생성 실패 memberChapterId={}", event.memberChapterId(), e);
        }
    }
}
