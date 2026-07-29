package com.umc.halo.global.seed;

import com.umc.halo.domain.content.chapter.entity.*;
import com.umc.halo.domain.content.chapter.repository.*;
import com.umc.halo.domain.content.storybook.entity.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.*;
import java.util.stream.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChapterSeeder {
    private final ChapterRepository chapterRepository;
    private final ChapterQuestionRepository chapterQuestionRepository;
    private final SceneCardRepository sceneCardRepository;

    @Transactional
    public List<Chapter> seedChapter(Storybook storybook) {
        List<Chapter> chapters = List.of(
                Chapter.builder()
                        .storybook(storybook)
                        .chapterOrder(1)
                        .title("집 앞 한 바퀴")
                        .imageUrl("https://example.com/chapter1.png")
                        .guide("오늘은 집 앞을 가볍게 걸어볼까요? 많은 말을 하지 않아도, 부모님의 걸음에 맞춰보는 것부터 시작해봐요.")
                        .shortDescription("부모님이 지금의 내 나이였을 때의 하루")
                        .description("부담 없이 집 앞을 함께 걸어보는 첫 장입니다. 대화보다 부모님의 걸음에 맞춰보는 것에서 시작합니다.")
                        .imageDescription("부모님이 내 나이였던 시절을 보여주는 사진이나 장면카드로 남겨보세요.")
                        .build(),
                Chapter.builder()
                        .storybook(storybook)
                        .chapterOrder(2)
                        .title("자주 가던 길")
                        .imageUrl("https://example.com/chapter2.png")
                        .guide("부모님이 자주 가는 길을 함께 가볼까요? 익숙한 길 안에도 내가 몰랐던 하루가 숨어 있을지 몰라요.")
                        .shortDescription("목차 예시2")
                        .description("부모님이 자주 가는 길을 함께 따라가봅니다. 익숙한 길 안에 숨어 있던 부모님의 일상을 발견합니다.")
                        .imageDescription("부모님이 자주 가던 길을 보여주는 사진이나 장면카드로 남겨보세요.")
                        .build(),
                Chapter.builder()
                        .storybook(storybook)
                        .chapterOrder(3)
                        .title("부모님의 속도")
                        .imageUrl("https://example.com/chapter3.png")
                        .guide("오늘은 부모님의 속도에 맞춰 걸어봐요. 어디서 천천히 걷고, 어디서 잠깐 쉬는지도 조용히 살펴봐요.")
                        .shortDescription("목차 예시3")
                        .description("부모님의 속도에 맞춰 걸어봐요. 부모님의 사소한 습관을 알아봅시다.")
                        .imageDescription("부모님의 걸음 속도가 느껴지는 사진이나 장면카드로 남겨보세요.")
                        .build(),
                Chapter.builder()
                        .storybook(storybook)
                        .chapterOrder(4)
                        .title("함께 장보기")
                        .imageUrl("https://example.com/chapter4.png")
                        .guide("부모님과 장을 보러 가볼까요? 어떤 걸 고르시는지 보고, 무거운 건 내가 먼저 들어드려봐요.")
                        .shortDescription("목차 예시4")
                        .description("부모님과 장을 보며 선택의 기준을 알아봅니다. 무거운 것을 먼저 들어드리며 작은 수고를 나눠봅니다.")
                        .imageDescription("부모님과 함께 고른 물건을 보여주는 사진이나 장면카드로 남겨보세요.")
                        .build(),
                Chapter.builder()
                        .storybook(storybook)
                        .chapterOrder(5)
                        .title("가까운 외출")
                        .imageUrl("https://example.com/chapter5.png")
                        .guide("멀리 가지 않아도 괜찮아요. 부모님이 편하게 다녀올 수 있는 곳으로 작은 외출을 해봐요.")
                        .shortDescription("목차 예시5")
                        .description("멀리 가지 않아도 가능한 작은 외출을 다녀옵니다. 부모님이 편하게 머물 수 있는 시간과 장소를 함께 경험합니다.")
                        .imageDescription("부모님과 다녀온 가까운 외출을 보여주는 사진이나 장면카드로 남겨보세요.")
                        .build(),
                Chapter.builder()
                        .storybook(storybook)
                        .chapterOrder(6)
                        .title("오래된 장소")
                        .imageUrl("https://example.com/chapter6.png")
                        .guide("부모님에게 의미 있는 장소가 있을까요? 가능하다면 함께 가보고, 그곳에 담긴 이야기를 천천히 들어봐요.")
                        .shortDescription("목차 예시6")
                        .description("부모님에게 의미 있는 장소를 찾아가봅니다. 그곳에 얽힌 기억을 들으며 부모님의 시간을 따라가봅니다.")
                        .imageDescription("부모님의 기억이 머문 장소를 보여주는 사진이나 장면카드로 남겨보세요.")
                        .build(),
                Chapter.builder()
                        .storybook(storybook)
                        .chapterOrder(7)
                        .title("오늘의 사진 한 장")
                        .imageUrl("https://example.com/chapter7.png")
                        .guide("오늘 부모님의 모습이 좋아 보인다면 사진 한 장 남겨볼까요? ‘찍어도 돼요?’ 하고 물어보면 더 자연스러워요.")
                        .shortDescription("목차 예시7")
                        .description("외출 중 부모님의 모습을 사진으로 남기는 장입니다. 오늘의 표정과 내가 좋아한 모습을 함께 기록합니다.")
                        .imageDescription("오늘의 부모님 모습이 담긴 사진이나 장면카드로 남겨보세요.")
                        .build(),
                Chapter.builder()
                        .storybook(storybook)
                        .chapterOrder(8)
                        .title("조금 먼 곳")
                        .imageUrl("https://example.com/chapter8.png")
                        .guide("평소보다 조금 먼 곳에 함께 가볼까요? 부모님이 힘들지 않도록 이동 시간과 쉬는 시간을 먼저 챙겨봐요.")
                        .shortDescription("목차 예시8")
                        .description("평소보다 조금 먼 곳을 함께 다녀옵니다. 부모님의 체력과 이동 시간을 살피며 무리 없는 추억을 만듭니다.")
                        .imageDescription("부모님과 조금 멀리 다녀온 곳을 보여주는 사진이나 장면카드로 남겨보세요.")
                        .build(),
                Chapter.builder()
                        .storybook(storybook)
                        .chapterOrder(9)
                        .title("돌아오는 길")
                        .imageUrl("https://example.com/chapter9.png")
                        .guide("돌아오는 길에 오늘 어땠는지 살짝 물어볼까요? 그 한마디가 다음 외출의 시작이 될 수 있어요.")
                        .shortDescription("목차 예시9")
                        .description("돌아오는 길에 오늘의 감상을 나눠봅니다. 부모님의 한마디를 통해 다음 외출의 힌트를 남깁니다.")
                        .imageDescription("하루를 마치고 돌아오는 길의 분위기를 보여주는 사진이나 장면카드로 남겨보세요.")
                        .build(),
                Chapter.builder()
                        .storybook(storybook)
                        .chapterOrder(10)
                        .title("나란히 걷는 우리")
                        .imageUrl("https://example.com/chapter10.png")
                        .guide("함께 걸었던 길들을 떠올려볼까요? 가장 좋았던 길과 다음에 또 걷고 싶은 곳도 남겨봐요.")
                        .shortDescription("목차 예시10")
                        .description("함께 걸었던 길들을 돌아보는 마지막 장입니다. 가장 좋았던 길과 앞으로 또 걷고 싶은 곳을 정리합니다.")
                        .imageDescription("부모님과 함께 걸어온 시간을 보여주는 사진이나 장면카드로 남겨보세요.")
                        .build()

        );
        List<Chapter> savedChapters = chapterRepository.saveAll(chapters);
        log.info("Chapters {}건 시딩 완료", savedChapters.size());

        return savedChapters;
    }

    // 5. 나란히 걷는 날 기준 질문(챕터 시드 데이터는 전체 스토리북이 공유)
    private static final List<List<String>> CHAPTER_QUESTIONS = List.of(
            // 1. 집 앞 한바퀴
            List.of(
                    "부모님과 함께 걸어 본 장소는 어디인가요?",
                    "걸으면서 어떤 이야기를 나눴나요?",
                    "부모님과 걸으며 느낀 감정은 무엇인가요?"
            ),
            // 2. 자주 가던 길
            List.of(
                    "함께 간 장소는 어디인가요?",
                    "부모님이 자주 가는 이유는 무엇인가요?",
                    "내가 몰랐던 일상은 무엇인가요?"
            ),
            // 3. 부모님의 속도
            List.of(
                    "부모님의 걸음 속도는 어땠나요?",
                    "배려가 필요했던 순간들은 언제인가요?",
                    "걸으며 느낀 점은 무엇인가요?"
            ),
            // 4. 함께 장보기
            List.of(
                    "함께 산 물건은 무엇인가요?",
                    "부모님이 고른 이유는 무엇인가요?",
                    "장을 함께 보며 부모님에게 도움이 된 일은 무엇인가요?"
            ),
            // 5. 가까운 외출
            List.of(
                    "함께 간 곳은 어디인가요?",
                    "부모님의 반응은 어땠나요?",
                    "좋았던 순간은 언제인가요?"
            ),
            // 6. 오래된 장소
            List.of(
                    "부모님에게 의미 있는 장소는 어디인가요?",
                    "그곳에 얽힌 기억은 무엇인가요?",
                    "새롭게 알게 된 이야기는 무엇인가요?"
            ),
            // 7. 오늘의 사진 한 장
            List.of(
                    "사진을 찍은 장소는 어디인가요?",
                    "사진 속 부모님의 표정은 어땠나요?",
                    "내가 좋아한 모습은 무엇인가요?"
            ),
            // 8. 조금 먼 곳
            List.of(
                    "함께 다녀온 목적지는 어디인가요?",
                    "준비 과정은 어땠나요?",
                    "기억에 남은 장면은 무엇인가요?"
            ),
            // 9. 돌아오는 길
            List.of(
                    "부모님이 남긴 한마디는 무엇인가요?",
                    "돌아오는 길의 내 기분은 어땠나요?",
                    "다음 외출은 어디로 할 예정인가요?"
            ),
            // 10. 나란히 걷는 우리
            List.of(
                    "가장 좋았던 길은 어디인가요?",
                    "부모님과 걸으며 느낀 점은 무엇인가요?",
                    "다음에는 어디로 걷고 싶나요?"
            )
    );

    @Transactional
    public List<ChapterQuestion> seedChapterQuestion(Chapter chapter, List<String> questions) {
        List<ChapterQuestion> chapterQuestions = IntStream.range(0, questions.size())
                .mapToObj(i -> ChapterQuestion.builder()
                        .chapter(chapter)
                        .question(questions.get(i))
                        .questionOrder(i + 1)
                        .build())
                .toList();
        List<ChapterQuestion> savedChapterQuestions = chapterQuestionRepository.saveAll(chapterQuestions);
        log.info("Chapters {}건 시딩 완료", savedChapterQuestions.size());

        return savedChapterQuestions;
    }

    @Transactional
    public List<SceneCard> seedSceneCard(Chapter chapter) {
        List<SceneCard> sceneCards = IntStream.range(1, 5)
                .mapToObj(i -> SceneCard.builder()
                        .chapter(chapter)
                        .imageUrl("https://example.com/scene" + i + ".png")
                        .build())
                .toList();
        List<SceneCard> savedSceneCards = sceneCardRepository.saveAll(sceneCards);
        log.info("SceneCards {}건 시딩 완료", savedSceneCards.size());

        return savedSceneCards;
    }

    @Transactional
    public void seed(Storybook storybook) {
        List<Chapter> chapters = seedChapter(storybook);
        IntStream.range(0, chapters.size()).forEach(i -> {
            Chapter chapter = chapters.get(i);
            seedChapterQuestion(chapter, CHAPTER_QUESTIONS.get(i));
            seedSceneCard(chapter);
        });
    }
}

