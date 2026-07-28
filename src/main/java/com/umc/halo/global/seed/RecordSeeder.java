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

    // chapterOrder(1~10) 기준 완료 챕터 AI 요약 예시 (김하로/오래 전 당신)
    private static final List<String> CHAPTER_SUMMARIES = List.of(
            "부모님과 함께 하천길을 걸으며 회사 일과 학창 시절 이야기를 나눴다. 오랜만에 둘만의 시간을 보내며 편안하고 따뜻한 마음을 느꼈다.",
            "동네 시장 골목길을 함께 걸으며, 매일 아침 그 길을 산책하신다는 부모님의 일상을 새롭게 알게 되었다.",
            "예전보다 느려진 부모님의 걸음에 맞춰 걸으며 평소엔 몰랐던 주변 풍경을 함께 발견했다.",
            "장을 보며 제철 나물과 두부를 함께 골랐고, 무거운 짐을 들어드리며 작은 도움을 드릴 수 있었다.",
            "부모님이 자주 가시던 동네 공원에 함께 가서 벤치에 앉아 하늘을 보며 옛 이야기를 나눴다.",
            "부모님이 결혼 전 자주 걸으셨다는 학교 앞 골목길에서, 그 시절 긴장했던 첫 데이트 이야기를 새롭게 들었다.",
            "벚꽃나무 아래에서 사진을 찍으며, 꽃을 보고 아이처럼 환하게 웃던 부모님의 모습을 마음에 담았다.",
            "저수지 둘레길을 함께 걸으며 노을을 바라보았고, 그 장면이 오래도록 기억에 남았다.",
            "함께 걸어서 좋다는 부모님의 한마디에 마음이 뿌듯해졌고, 다음엔 수목원에 가기로 약속했다.",
            "지금까지의 산책길을 돌아보며, 부모님과 함께 걷는 시간 자체가 소중하다는 것을 깨달았다."
    );

    // 질문 내용에 맞춘 답변 예시 (ChapterSeeder.CHAPTER_QUESTIONS와 1:1 매칭)
    private static final Map<String, String> QUESTION_ANSWERS = Map.ofEntries(
            Map.entry("부모님과 함께 걸어 본 장소는 어디인가요?", "집 근처 하천길을 함께 걸었어요. 저녁 노을이 예뻐서 천천히 걸었던 기억이 나요."),
            Map.entry("걸으면서 어떤 이야기를 나눴나요?", "요즘 회사 일이랑 옛날 학창 시절 얘기를 편하게 나눴어요."),
            Map.entry("부모님과 걸으며 느낀 감정은 무엇인가요?", "오랜만에 둘이서만 시간을 보내니까 마음이 편안하고 따뜻했어요."),
            Map.entry("함께 간 장소는 어디인가요?", "동네 시장 근처 골목길이었어요."),
            Map.entry("부모님이 자주 가는 이유는 무엇인가요?", "예전부터 조용하고 사람이 적어서 마음이 편해진다고 하셨어요."),
            Map.entry("내가 몰랐던 일상은 무엇인가요?", "매일 아침 그 길을 산책하면서 하루를 시작하신다는 걸 처음 알았어요."),
            Map.entry("부모님의 걸음 속도는 어땠나요?", "예전보다 조금 느려지셨지만, 그만큼 주변을 더 자세히 보시는 것 같았어요."),
            Map.entry("배려가 필요했던 순간들은 언제인가요?", "횡단보도를 건널 때랑 계단을 오를 때 살짝 속도를 맞춰드렸어요."),
            Map.entry("걸으며 느낀 점은 무엇인가요?", "부모님 걸음에 맞춰 걷다 보니 평소엔 못 봤던 것들이 눈에 들어왔어요."),
            Map.entry("함께 산 물건은 무엇인가요?", "저녁 반찬거리로 제철 나물이랑 두부를 샀어요."),
            Map.entry("부모님이 고른 이유는 무엇인가요?", "가격도 적당하고 오래 보관할 수 있어서 고르셨대요."),
            Map.entry("장을 함께 보며 부모님에게 도움이 된 일은 무엇인가요?", "무거운 짐을 대신 들어드린 게 도움이 됐던 것 같아요."),
            Map.entry("함께 간 곳은 어디인가요?", "예전에 자주 가시던 동네 공원에 함께 갔어요."),
            Map.entry("부모님의 반응은 어땠나요?", "오랜만에 와봤다며 반가워하시고 옛날 얘기를 많이 해주셨어요."),
            Map.entry("좋았던 순간은 언제인가요?", "벤치에 앉아서 같이 하늘을 보던 순간이 제일 좋았어요."),
            Map.entry("부모님에게 의미 있는 장소는 어디인가요?", "결혼 전에 자주 걸으셨다는 학교 앞 골목길이었어요."),
            Map.entry("그곳에 얽힌 기억은 무엇인가요?", "그 길에서 첫 데이트를 하셨다는 이야기를 해주셨어요."),
            Map.entry("새롭게 알게 된 이야기는 무엇인가요?", "그 시절 많이 긴장하셨었다는 걸 처음 알게 됐어요."),
            Map.entry("사진을 찍은 장소는 어디인가요?", "산책길에 있는 벚꽃나무 아래에서 찍었어요."),
            Map.entry("사진 속 부모님의 표정은 어땠나요?", "환하게 웃고 계셔서 보는 저도 기분이 좋아졌어요."),
            Map.entry("내가 좋아한 모습은 무엇인가요?", "꽃을 보며 아이처럼 좋아하시던 모습이 좋았어요."),
            Map.entry("함께 다녀온 목적지는 어디인가요?", "근처 저수지 둘레길에 다녀왔어요."),
            Map.entry("준비 과정은 어땠나요?", "날씨를 미리 확인하고 편한 신발을 챙겨 나섰어요."),
            Map.entry("기억에 남은 장면은 무엇인가요?", "노을 지는 저수지를 나란히 바라보던 장면이 기억에 남아요."),
            Map.entry("부모님이 남긴 한마디는 무엇인가요?", "\"이렇게 같이 걸으니까 참 좋다\"라고 하셨어요."),
            Map.entry("돌아오는 길의 내 기분은 어땠나요?", "마음이 뿌듯하고 따뜻한 기분으로 돌아왔어요."),
            Map.entry("다음 외출은 어디로 할 예정인가요?", "다음엔 근처 수목원에 같이 가보기로 했어요."),
            Map.entry("가장 좋았던 길은 어디인가요?", "처음 함께 걸었던 하천길이 가장 기억에 남아요."),
            Map.entry("부모님과 걸으며 느낀 점은 무엇인가요?", "함께 걷는 시간 자체가 소중하다는 걸 깨달았어요."),
            Map.entry("다음에는 어디로 걷고 싶나요?", "다음엔 좀 더 먼 곳까지 함께 걸어보고 싶어요.")
    );

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

        Status status = Status.COMPLETED;

        return MemberChapter.builder()
                .member(member)
                .storybookChapter(storybookChapter)
                .sceneCard(sceneCard)
                .emotion(emotion)
                .coverType(CoverType.SCENE_CARD)
                .completedDate(completedDate)
                .summary(status == Status.COMPLETED ? CHAPTER_SUMMARIES.get(chapterOrder - 1) : null)
                .status(status)
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
                                    .answer(QUESTION_ANSWERS.getOrDefault(question.getQuestion(),
                                            memberChapter.getMember().getName() + "의 답변입니다."))
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