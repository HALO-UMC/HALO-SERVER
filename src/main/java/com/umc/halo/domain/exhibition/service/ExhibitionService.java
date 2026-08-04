package com.umc.halo.domain.exhibition.service;

import com.umc.halo.domain.content.chapter.entity.Chapter;
import com.umc.halo.domain.content.chapter.entity.SceneCard;
import com.umc.halo.domain.content.chapter.repository.ChapterRepository;
import com.umc.halo.domain.content.storybook.entity.Storybook;
import com.umc.halo.domain.content.storybook.entity.StorybookCharacterVariant;
import com.umc.halo.domain.content.storybook.enums.Variant;
import com.umc.halo.domain.content.storybook.exception.StorybookException;
import com.umc.halo.domain.content.storybook.exception.code.StorybookErrorCode;
import com.umc.halo.domain.content.storybook.repository.StorybookCharacterVariantRepository;
import com.umc.halo.domain.content.storybook.service.StorybookService;
import com.umc.halo.domain.exhibition.converter.ExhibitionConverter;
import com.umc.halo.domain.exhibition.dto.ExhibitionChapterResDTO;
import com.umc.halo.domain.exhibition.dto.ExhibitionResDTO;
import com.umc.halo.domain.exhibition.exception.ExhibitionException;
import com.umc.halo.domain.exhibition.exception.code.ExhibitionErrorCode;
import com.umc.halo.domain.image.service.ImageService;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.exception.MemberException;
import com.umc.halo.domain.member.exception.code.MemberErrorCode;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.record.entity.MemberChapter;
import com.umc.halo.domain.record.entity.MemberStorybook;
import com.umc.halo.domain.record.enums.CoverType;
import com.umc.halo.domain.record.enums.Status;
import com.umc.halo.domain.record.repository.MemberChapterRepository;
import com.umc.halo.domain.record.repository.MemberStorybookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExhibitionService {

    private final ImageService imageService;
    private final MemberRepository memberRepository;
    private final MemberStorybookRepository memberStorybookRepository;
    private final MemberChapterRepository memberChapterRepository;
    private final ChapterRepository chapterRepository;
    private final StorybookCharacterVariantRepository storybookCharacterVariantRepository;
    private final StorybookService storybookService;

    public ExhibitionResDTO.MainInfo getExhibition(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        List<MemberStorybook> memberStorybooks =
                memberStorybookRepository.findAllByMemberWithStorybook(member);
        List<MemberStorybook> completed = new ArrayList<>();
        List<MemberStorybook> inProgress = new ArrayList<>();
        Map<Long, List<MemberChapter>> chaptersByStorybook = Map.of();

        if (!memberStorybooks.isEmpty()) {
            List<Long> storybookIds = memberStorybooks.stream()
                    .map(ms -> ms.getStorybook().getId())
                    .toList();

            Map<Long, Long> totalChapterCounts = chapterRepository
                    .findByStorybook_IdIn(storybookIds).stream()
                    .collect(Collectors.groupingBy(
                            sc -> sc.getStorybook().getId(), Collectors.counting()));

            chaptersByStorybook = memberChapterRepository
                    .findAllByMemberWithChapter(member).stream()
                    .collect(Collectors.groupingBy(
                            mc -> mc.getChapter().getStorybook().getId()));

            for (MemberStorybook ms : memberStorybooks) {
                Long storybookId = ms.getStorybook().getId();
                long totalChapters = totalChapterCounts.getOrDefault(storybookId, 0L);
                long completedCount = chaptersByStorybook.getOrDefault(storybookId, List.of()).stream()
                        .filter(mc -> mc.getStatus() == Status.COMPLETED)
                        .count();

                if (totalChapters > 0 && completedCount == totalChapters) {
                    completed.add(ms);
                } else {
                    inProgress.add(ms);
                }
            }
        }
        int collectedCharacterCount = completed.size();
        int inProgressStorybookCount = inProgress.size();
        List<ExhibitionResDTO.CompletedStorybook> completedDtos = new ArrayList<>();
        List<ExhibitionResDTO.InProgressStorybook> inProgressDtos = new ArrayList<>();
        List<ExhibitionResDTO.RecommendedStorybook> recommendedDtos = new ArrayList<>();
        Long currentStorybookId = null;

        if (!completed.isEmpty()) {
            completed.sort(Comparator.comparing(MemberStorybook::getLastCompletedDate)
                    .thenComparing(ms -> ms.getStorybook().getId())
                    .reversed());
            currentStorybookId = completed.get(0).getStorybook().getId();

            List<Storybook> completedStorybooks = completed.stream()
                    .map(MemberStorybook::getStorybook)
                    .toList();
            Map<Long, StorybookCharacterVariant> characterVariantMap = storybookCharacterVariantRepository
                    .findAllByStorybookCharacter_StorybookInAndVariant(completedStorybooks, Variant.EXHIBITION).stream()
                    .collect(Collectors.toMap(c -> c.getStorybookCharacter().getStorybook().getId(), Function.identity()));

            for (MemberStorybook ms : completed) {
                StorybookCharacterVariant characterVariant = characterVariantMap.get(ms.getStorybook().getId());
                if (characterVariant == null) {
                    throw new StorybookException(StorybookErrorCode.NOT_FOUND_CHARACTER);
                }
                completedDtos.add(ExhibitionConverter.toCompletedStorybook(ms, characterVariant));
            }
        } else if (!inProgress.isEmpty()) {
            for (MemberStorybook ms : inProgress) {
                inProgressDtos.add(ExhibitionConverter.toInProgressStorybook(
                        ms, resolveCompletedChapterOrder(ms)));
            }
        } else {
            recommendedDtos = storybookService.getRecommendedStorybooks(memberId).storybooks().stream()
                    .map(ExhibitionConverter::toRecommendedStorybook)
                    .toList();
        }

        return ExhibitionConverter.toMainInfo(
                collectedCharacterCount, inProgressStorybookCount,
                currentStorybookId, completedDtos, inProgressDtos, recommendedDtos
        );
    }

    public ExhibitionChapterResDTO.ChaptersInfo getChapters(Long memberId, Long storybookId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        memberStorybookRepository.findAllByMemberWithStorybook(member).stream()
                .filter(ms -> ms.getStorybook().getId().equals(storybookId))
                .findFirst()
                .orElseThrow(() -> new ExhibitionException(ExhibitionErrorCode.NOT_FOUND));


        List<Chapter> chapters = chapterRepository
                .findByStorybook_IdOrderByChapterOrderAsc(storybookId);

        Map<Long, MemberChapter> myChapters = memberChapterRepository
                .findAllByMemberAndStorybookIdWithSceneCard(member, storybookId).stream()
                .collect(Collectors.toMap(
                        mc -> mc.getChapter().getId(),
                        Function.identity()
                ));

        long completedCount = myChapters.values().stream()
                .filter(mc -> mc.getStatus() == Status.COMPLETED)
                .count();
        if (chapters.isEmpty() || completedCount != chapters.size()) {
            throw new ExhibitionException(ExhibitionErrorCode.NOT_COMPLETED);
        }

        List<ExhibitionChapterResDTO.ChapterInfo> chapterInfos = chapters.stream()
                .map(c -> {
                    MemberChapter mc = myChapters.get(c.getId());
                    return ExhibitionConverter.toChapterInfo(c, mc, resolveChapterImageUrl(mc));
                })
                .toList();

        return ExhibitionConverter.toChaptersInfo(storybookId, chapterInfos);
    }

    private String resolveChapterImageUrl(MemberChapter mc) {
        if (mc.getCoverType() == CoverType.IMAGE && mc.getImageKey() != null) {
            return imageService.getImage(mc.getImageKey());
        }
        SceneCard sceneCard = mc.getSceneCard();
        return sceneCard == null ? null : sceneCard.getImageUrl();
    }

    private Integer resolveCompletedChapterOrder(MemberStorybook ms) {
        Integer lastChapterOrder = ms.getLastChapterOrder();
        return lastChapterOrder == null ? 0 : lastChapterOrder;
    }
}