package com.umc.halo.global.seed;

import com.umc.halo.domain.content.chapter.entity.*;
import com.umc.halo.domain.content.chapter.repository.*;
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
    public List<Chapter> seedChapter() {
        List<Chapter> chapters = List.of(
                Chapter.builder()
                        .title("나와 같은 나이였던 시절")
                        .imageUrl("https://example.com/chapter1.png")
                        .guide("부모님이 지금 내 나이였을 땐 어떤 하루를 보냈을까요? 사진 한 장을 보며 가볍게 물어봐요.")
                        .shortDescription("부모님이 지금의 내 나이였을 때의 하루")
                        .description("부모님을 한 사람으로 바라보는 첫 장입니다. 지금의 내 나이였을 때 부모님은 어떤 하루를 살고 있었는지 돌아봅니다.")
                        .imageDescription("부모님이 내 나이였던 시절을 보여주는 사진이나 장면카드로 남겨보세요.")
                        .build(),
                Chapter.builder()
                        .title("소년과 소녀의 꿈")
                        .imageUrl("https://example.com/chapter2.png")
                        .guide("어릴 적 부모님은 어떤 꿈을 꾸셨을까요? 좋아했던 놀이부터 천천히 들어보면 어떨까요?")
                        .shortDescription("목차 예시2")
                        .description("부모님에게도 작고 선명했던 어린 시절의 꿈이 있었습니다. 무엇을 좋아하고 어떤 사람이 되고 싶었는지 들어봅니다.")
                        .imageDescription("부모님의 어린 시절 꿈을 보여주는 사진이나 장면카드로 남겨보세요.")
                        .build(),
                Chapter.builder()
                        .title("빛나던 청춘의 한 페이지")
                        .imageUrl("https://example.com/chapter3.png")
                        .guide("부모님의 젊은 날이 담긴 사진을 함께 골라볼까요? 사진 속 표정과 이야기를 천천히 들어봐요.")
                        .shortDescription("목차 예시3")
                        .description("부모님의 젊은 날이 담긴 사진 속으로 들어가보는 장입니다. 사진 속 장소와 사람들을 통해 내가 몰랐던 부모님의 표정을 발견합니다.")
                        .imageDescription("부모님의 청춘이 가장 선명하게 보이는 사진이나 장면카드로 남겨보세요.")
                        .build(),
                Chapter.builder()
                        .title("첫 출근과 첫 월급")
                        .imageUrl("https://example.com/chapter4.png")
                        .guide("처음 일하러 가던 날, 어떤 마음이셨을까요? 첫 월급 이야기도 살짝 물어보면 좋겠어요.")
                        .shortDescription("목차 예시4")
                        .description("부모님이 처음 사회에 나섰던 순간을 돌아봅니다. 첫 출근의 마음과 첫 월급의 기억을 통해 부모님의 시작점을 기록합니다.")
                        .imageDescription("부모님의 첫 시작을 보여주는 사진이나 장면카드로 남겨보세요.")
                        .build(),
                Chapter.builder()
                        .title("두 사람의 서막")
                        .imageUrl("https://example.com/chapter5.png")
                        .guide("부모님은 처음 어떻게 만나셨을까요? 첫인상부터 조심스럽게 물어보며 가족의 시작을 떠올려봐요.")
                        .shortDescription("목차 예시5")
                        .description("부모님이 서로를 만나 가족이 시작되기 전의 이야기를 들어봅니다. 첫 만남과 첫인상을 통해 우리 가족의 첫 장면을 떠올립니다.")
                        .imageDescription("두 분의 시작을 보여주는 사진이나 장면카드로 남겨보세요.")
                        .build(),
                Chapter.builder()
                        .title("가장 용기 냈던 순간")
                        .imageUrl("https://example.com/chapter6.png")
                        .guide("부모님이 살면서 가장 용기 냈던 선택은 무엇이었을까요? 그때의 마음을 천천히 들어봐요.")
                        .shortDescription("목차 예시6")
                        .description("부모님이 삶에서 큰 결정을 내렸던 순간을 돌아봅니다. 그때의 고민과 선택을 들으며 부모님의 용기를 이해해봅니다.")
                        .imageDescription("부모님이 용기 냈던 선택을 떠올리게 하는 사진이나 장면카드로 남겨보세요.")
                        .build(),
                Chapter.builder()
                        .title("잊지 못할 사람들")
                        .imageUrl("https://example.com/chapter7.png")
                        .guide("부모님 마음속에 오래 남아 있는 사람은 누구일까요? 어떤 사람이었는지 함께 떠올려보세요!")
                        .shortDescription("목차 예시7")
                        .description("부모님 마음속에 오래 남아 있는 사람을 만나보는 장입니다. 그 사람이 어떤 의미였는지 들으며 부모님의 관계와 시간을 알아갑니다.")
                        .imageDescription("부모님 마음에 오래 남은 사람을 보여주는 사진이나 장면카드로 남겨보세요.")
                        .build(),
                Chapter.builder()
                        .title("부모가 되기 전의 밤")
                        .imageUrl("https://example.com/chapter8.png")
                        .guide("내가 태어나기 전, 부모님은 어떤 마음으로 기다리고 계셨을까요? 조심스럽게 물어봐요.")
                        .shortDescription("목차 예시8")
                        .description("내가 태어나기 전 부모님의 마음을 조심스럽게 들어봅니다. 기다림과 걱정, 설렘이 섞여 있던 시간을 기록합니다.")
                        .imageDescription("부모가 되기 전후의 마음을 보여주는 사진이나 장면카드로 남겨보세요.")
                        .build(),
                Chapter.builder()
                        .title("잠시 접어두었던 것들")
                        .imageUrl("https://example.com/chapter9.png")
                        .guide("부모님이 예전에 좋아했지만 잠시 미뤄둔 것이 있을까요? 다시 해보고 싶은지도 살짝 물어봐요.")
                        .shortDescription("목차 예시9")
                        .description("부모님이 가족을 위해 잠시 미뤄두었던 것들을 떠올려봅니다. 내려놓았던 꿈이나 취미를 통해 부모님의 또 다른 모습을 발견합니다.")
                        .imageDescription("부모님이 잠시 내려놓았던 것을 보여주는 사진이나 장면카드로 남겨보세요.")
                        .build(),
                Chapter.builder()
                        .title("다시 쓰는 당신의 프로필")
                        .imageUrl("https://example.com/chapter10.png")
                        .guide("앞의 이야기를 떠올리며 부모님을 한 사람으로 소개해볼까요? 새롭게 알게 된 마음도 함께 남겨봐요.")
                        .shortDescription("목차 예시10")
                        .description("앞의 기록을 바탕으로 부모님을 다시 소개하는 마지막 장입니다. 누구의 부모가 아닌 한 사람으로서의 부모님을 정리합니다.")
                        .imageDescription("지금의 부모님을 보여주는 사진이나 장면카드로 남겨보세요.")
                        .build()

        );
        List<Chapter> savedChapters = chapterRepository.saveAll(chapters);
        log.info("Chapters {}건 시딩 완료", savedChapters.size());

        return savedChapters;
    }

    @Transactional
    public List<ChapterQuestion> seedChapterQuestion(Chapter chapter) {
        List<ChapterQuestion> chapterQuestions = IntStream.range(1, 4)
                .mapToObj(i -> ChapterQuestion.builder()
                        .chapter(chapter)
                        .question(chapter.getTitle() + "에 대한 질문" + i)
                        .questionOrder(i)
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
    public void seed() {
        List<Chapter> chapters = seedChapter();
        chapters.forEach(chapter -> {
            seedChapterQuestion(chapter);
            seedSceneCard(chapter);
        });
    }
}

