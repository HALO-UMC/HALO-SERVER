package com.umc.halo.global.seed;

import com.umc.halo.domain.content.chapter.entity.*;
import com.umc.halo.domain.content.chapter.repository.*;
import com.umc.halo.domain.content.storybook.entity.*;
import com.umc.halo.domain.content.storybook.repository.*;
import com.umc.halo.domain.member.entity.*;
import com.umc.halo.domain.member.repository.*;
import com.umc.halo.domain.record.entity.*;
import com.umc.halo.domain.record.enums.*;
import com.umc.halo.domain.record.repository.*;
import com.umc.halo.global.enums.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class RecordSeeder {
    private final MemberChapterAnswerRepository memberChapterAnswerRepository;
    private final MemberChapterRepository memberChapterRepository;
    private final MemberStorybookRepository memberStorybookRepository;
    private final MemberRepository memberRepository;
    private final StorybookRepository storybookRepository;
    private final StorybookChapterRepository storybookChapterRepository;
    private final ChapterQuestionRepository chapterQuestionRepository;
    private final SceneCardRepository sceneCardRepository;

    // 회원별 스토리북 진행 시나리오
    private record ProgressScenario(String memberName, String storybookTitle, int lastChapterOrder,
                                    Emotion emotion, LocalDate completedDate) {
    }

    private static final List<ProgressScenario> PROGRESS_SCENARIOS = List.of(
            new ProgressScenario("김하로", "오래 전 당신", 10, Emotion.HAPPY, LocalDate.now()), // 완료
            new ProgressScenario("김하로", "취향이 닿는 날", 3, Emotion.THOUGHTFUL, LocalDate.now()), // 진행중
            new ProgressScenario("이온", "나란히 걷는 날", 9, Emotion.GRATEFUL, LocalDate.now().minusDays(1)) // 진행중 (1장 남음)
    );

    @Transactional
    public List<MemberStorybook> seedMemberStorybook(Map<String, Member> member, Map<String, Storybook> storybook) {
        List<MemberStorybook> memberStorybooks = PROGRESS_SCENARIOS.stream()
                .map(scenario -> MemberStorybook.builder()
                        .member(member.get(scenario.memberName()))
                        .storybook(storybook.get(scenario.storybookTitle()))
                        .lastChapterOrder(scenario.lastChapterOrder())
                        .lastCompletedDate(scenario.completedDate())
                        .emotion(scenario.emotion())
                        .build())
                .toList();

        List<MemberStorybook> savedMemberStorybooks = memberStorybookRepository.saveAll(memberStorybooks);
        log.info("MemberStorybook {}건 시딩 완료", savedMemberStorybooks.size());

        return savedMemberStorybooks;
    }

    @Transactional
    public List<MemberChapter> seedMemberChapter(Map<String, Member> member, List<StorybookChapter> storybookChapters, List<SceneCard> sceneCards) {
        List<MemberChapter> memberChapters = PROGRESS_SCENARIOS.stream()
                .flatMap(scenario -> IntStream.rangeClosed(1, scenario.lastChapterOrder())
                        .mapToObj(chapterOrder -> buildMemberChapter(
                                member.get(scenario.memberName()),
                                scenario.storybookTitle(),
                                chapterOrder,
                                storybookChapters,
                                sceneCards,
                                scenario.emotion(),
                                scenario.completedDate())))
                .toList();

        List<MemberChapter> savedMemberChapters = memberChapterRepository.saveAll(memberChapters);
        log.info("MemberChapter {}건 시딩 완료", savedMemberChapters.size());

        return savedMemberChapters;
    }

    private MemberChapter buildMemberChapter(Member member, String storybookTitle, int chapterOrder,
                                             List<StorybookChapter> storybookChapters, List<SceneCard> sceneCards,
                                             Emotion emotion, LocalDate completedDate) {
        StorybookChapter storybookChapter = storybookChapters.stream()
                .filter(sc -> sc.getStorybook().getTitle().equals(storybookTitle)
                        && sc.getChapterOrder().equals(chapterOrder))
                .findFirst()
                .orElseThrow();

        SceneCard sceneCard = sceneCards.stream()
                .filter(sc -> sc.getChapter().getId().equals(storybookChapter.getChapter().getId()))
                .findFirst()
                .orElseThrow();

        return MemberChapter.builder()
                .member(member)
                .storybookChapter(storybookChapter)
                .sceneCard(sceneCard)
                .emotion(emotion)
                .coverType(CoverType.SCENE_CARD)
                .completedDate(completedDate)
                .summary("ai로 요약한 완료 기록")
                .status(Status.COMPLETED)
                .build();
    }

    @Transactional
    public List<MemberChapterAnswer> seedMemberChapterAnswer(List<MemberChapter> memberChapters, List<ChapterQuestion> chapterQuestions) {
        List<MemberChapterAnswer> memberChapterAnswers = memberChapters.stream()
                .flatMap(memberChapter -> {
                    Long chapterId = memberChapter.getStorybookChapter().getChapter().getId();
                    return chapterQuestions.stream()
                            .filter(question -> question.getChapter().getId().equals(chapterId))
                            .map(question -> MemberChapterAnswer.builder()
                                    .memberChapter(memberChapter)
                                    .chapterQuestion(question)
                                    .answer(memberChapter.getMember().getName() + "의 답변입니다.")
                                    .build());
                })
                .toList();

        List<MemberChapterAnswer> savedMemberChapterAnswers = memberChapterAnswerRepository.saveAll(memberChapterAnswers);
        log.info("MemberChapterAnswer {}건 시딩 완료", savedMemberChapterAnswers.size());

        return savedMemberChapterAnswers;
    }

    @Transactional
    public void seed() {
        Map<String, Member> member = memberRepository.findAll().stream()
                .collect(Collectors.toMap(Member::getName, Function.identity(), (a, b) -> a));
        Map<String, Storybook> storybook = storybookRepository.findAll().stream()
                .collect(Collectors.toMap(Storybook::getTitle, Function.identity()));
        List<StorybookChapter> storybookChapters = storybookChapterRepository.findAll();
        List<ChapterQuestion> chapterQuestions = chapterQuestionRepository.findAll();
        List<SceneCard> sceneCards = sceneCardRepository.findAll();

        seedMemberStorybook(member, storybook);
        List<MemberChapter> memberChapters = seedMemberChapter(member, storybookChapters, sceneCards);
        seedMemberChapterAnswer(memberChapters, chapterQuestions);
    }
}