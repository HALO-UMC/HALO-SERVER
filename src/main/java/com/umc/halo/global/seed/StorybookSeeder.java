package com.umc.halo.global.seed;

import com.umc.halo.domain.content.storybook.entity.*;
import com.umc.halo.domain.content.storybook.enums.*;
import com.umc.halo.domain.content.storybook.repository.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class StorybookSeeder {
    private final StorybookRepository storybookRepository;
    private final StorybookCharacterRepository storybookCharacterRepository;
    private final StorybookCharacterVariantRepository storybookCharacterVariantRepository;
    private final ChapterSeeder chapterSeeder;

    @Transactional
    public List<Storybook> seedStorybooks() {
        List<Storybook> storybooks = List.of(
                Storybook.builder()
                        .title("오래 전 당신")
                        .recommendationPhrase("부모님의 지난 시간을 알고 싶다면")
                        .shortDescription("가족과의 만남")
                        .description("부모님을 ‘부모'가 아닌 한 사람으로 바라보며, 어린 시절의 기억과 \n" +
                                "청춘의 순간, 지나온 시간을 차근차근 들어보는 이야기입니다. \n" +
                                "지금의 부모님을 이해하고, 더 가까워지는 시간을 선물해보세요.")
                        .imageUrl("https://example.com/storybook1.png")
                        .build(),

                Storybook.builder()
                        .title("당신 사용설명서")
                        .recommendationPhrase("부모님을 더 잘 이해하고 싶다면")
                        .shortDescription("나를 아는 시간")
                        .description("좋아하는 것과 싫어하는 것, 습관과 취향을 하나씩 들여다보며 \n" +
                                "스스로를 정리해보는 이야기입니다.")
                        .imageUrl("https://example.com/storybook2.png")
                        .build(),

                Storybook.builder()
                        .title("가족의 온도")
                        .recommendationPhrase("가족의 마음을 다시 느끼고 싶다면")
                        .shortDescription("가족이 서로에게 건네는 마음")
                        .description("무심코 지나쳤던 가족의 온기를 다시 느껴보는 이야기입니다. \n" +
                                "서로에게 전하지 못했던 마음을 꺼내어 가족 사이의 거리를 좁혀봅니다.")
                        .imageUrl("https://example.com/storybook3.png")
                        .build(),

                Storybook.builder()
                        .title("취향이 닿는 날")
                        .recommendationPhrase("함께할 취향을 찾고 싶다면")
                        .shortDescription("서로의 취향을 알아가는 시간")
                        .description("좋아하는 것과 싫어하는 것을 하나씩 나누며, \n" +
                                "서로의 취향이 닿는 순간들을 발견하는 이야기입니다.")
                        .imageUrl("https://example.com/storybook4.png")
                        .build(),

                Storybook.builder()
                        .title("나란히 걷는 날")
                        .recommendationPhrase("함께하는 시간을 늘리고 싶다면")
                        .shortDescription("함께 걸어온 시간을 돌아보는 이야기")
                        .description("누군가와 나란히 걸어온 시간들을 돌아보며, \n" +
                                "곁에 있어준 존재의 의미를 다시 새겨보는 이야기입니다.")
                        .imageUrl("https://example.com/storybook5.png")
                        .build(),

                Storybook.builder()
                        .title("오늘은 내가 먼저")
                        .recommendationPhrase("먼저 마음을 표현하고 싶다면")
                        .shortDescription("먼저 다가서는 용기에 대한 이야기")
                        .description("망설이던 마음을 내려놓고 오늘만큼은 내가 먼저 다가서 보는 이야기입니다. \n" +
                                "작은 용기가 만들어내는 변화를 기록해보세요.")
                        .imageUrl("https://example.com/storybook6.png")
                        .build()
        );

        List<Storybook> savedStorybooks = storybookRepository.saveAll(storybooks);
        log.info("Storybooks {}건 시딩 완료", savedStorybooks.size());

        return savedStorybooks;
    }

    private static final List<String> CHARACTER_NAMES = List.of("하로", "온이", "다솜", "결", "다온", "가온");

    @Transactional
    public StorybookCharacter seedStorybookCharacter(Storybook storybook, String characterName) {
        StorybookCharacter storybookCharacter = storybookCharacterRepository.save(
                StorybookCharacter.builder()
                        .storybook(storybook)
                        .name(characterName)
                        .build()
        );

        List<StorybookCharacterVariant> variants = List.of(
                StorybookCharacterVariant.builder()
                        .storybookCharacter(storybookCharacter)
                        .variant(Variant.WRITING)
                        .imageUrl("https://example.com/character-writing.png")
                        .build(),
                StorybookCharacterVariant.builder()
                        .storybookCharacter(storybookCharacter)
                        .variant(Variant.SCENE_CARD)
                        .imageUrl("https://example.com/character-scene-card.png")
                        .build(),
                StorybookCharacterVariant.builder()
                        .storybookCharacter(storybookCharacter)
                        .variant(Variant.EXHIBITION)
                        .imageUrl("https://example.com/character-exhibition.png")
                        .build(),
                StorybookCharacterVariant.builder()
                        .storybookCharacter(storybookCharacter)
                        .variant(Variant.PROFILE)
                        .imageUrl("https://example.com/character-profile.png")
                        .build()
        );

        storybookCharacterVariantRepository.saveAll(variants);
        log.info("StorybookCharacter/Variant 시딩 완료 (character: {}, variant {}건)", characterName, variants.size());

        return storybookCharacter;
    }

    @Transactional
    public void seed() {
        List<Storybook> storybooks = seedStorybooks();

        for (int i = 0; i < storybooks.size(); i++) {
            Storybook storybook = storybooks.get(i);
            chapterSeeder.seed(storybook);
            seedStorybookCharacter(storybook, CHARACTER_NAMES.get(i));
        }
    }
}