package com.umc.halo.domain.content.storybook.service;

import com.umc.halo.domain.content.storybook.converter.StorybookConverter;
import com.umc.halo.domain.content.storybook.dto.StorybookResDTO;
import com.umc.halo.domain.content.storybook.exception.code.StorybookErrorCode;
import com.umc.halo.domain.content.storybook.exception.StorybookException;
import com.umc.halo.domain.content.storybook.entity.Storybook;
import com.umc.halo.domain.content.storybook.entity.StorybookChapter;
import com.umc.halo.domain.content.storybook.enums.BookshelfStatus;
import com.umc.halo.domain.content.storybook.enums.ChapterViewStatus;
import com.umc.halo.domain.content.storybook.enums.HomeStatus;
import com.umc.halo.domain.content.storybook.enums.StorybookStatus;
import com.umc.halo.domain.content.storybook.repository.StorybookChapterRepository;
import com.umc.halo.domain.content.storybook.repository.StorybookRepository;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.exception.MemberException;
import com.umc.halo.domain.member.exception.code.MemberErrorCode;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.record.entity.MemberChapter;
import com.umc.halo.domain.record.entity.MemberStorybook;
import com.umc.halo.domain.record.enums.Status;
import com.umc.halo.domain.record.repository.MemberChapterRepository;
import com.umc.halo.domain.record.repository.MemberStorybookRepository;
import com.umc.halo.domain.tag.entity.MemberTag;
import com.umc.halo.domain.tag.entity.StorybookTag;
import com.umc.halo.domain.tag.entity.Tag;
import com.umc.halo.domain.tag.enums.Category;
import com.umc.halo.domain.tag.enums.PriorityLevel;
import com.umc.halo.domain.tag.repository.MemberTagRepository;
import com.umc.halo.domain.tag.repository.StorybookTagRepository;
import com.umc.halo.domain.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StorybookService {

    private final StorybookRepository storybookRepository;
    private final StorybookChapterRepository storybookChapterRepository;
    private final MemberChapterRepository memberChapterRepository;
    private final MemberRepository memberRepository;
    private final MemberStorybookRepository memberStorybookRepository;
    private final TagRepository tagRepository;
    private final StorybookTagRepository storybookTagRepository;
    private final MemberTagRepository memberTagRepository;

    public StorybookResDTO.GetStorybookDetail getStorybookDetail(Long storybookId, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        Storybook storybook = storybookRepository.findById(storybookId)
                .orElseThrow(() -> new StorybookException(StorybookErrorCode.NOT_FOUND));

        List<StorybookChapter> storybookChapters =
                storybookChapterRepository.findByStorybook_IdOrderByChapterOrderAsc(storybookId);

        List<MemberChapter> memberChapters =
                memberChapterRepository.findByMemberAndStorybookChapter_Storybook_Id(member, storybookId);

        Optional<MemberStorybook> memberStorybookOpt =
                memberStorybookRepository.findByMemberAndStorybook(member, storybook);

        Set<Long> completedChapterIds = memberChapters.stream()
                .filter(mc -> mc.getStatus() == Status.COMPLETED)
                .map(mc -> mc.getStorybookChapter().getId())
                .collect(Collectors.toSet());

        boolean completedToday = memberStorybookOpt
                .map(MemberStorybook::isCompletedToday)
                .orElse(false);

        boolean foundToday = false;
        List<StorybookResDTO.ChapterInfo> chapterInfos = new ArrayList<>();

        for (StorybookChapter sc : storybookChapters) {
            ChapterViewStatus status;
            if (completedChapterIds.contains(sc.getId())) {
                status = ChapterViewStatus.COMPLETED;
            } else if (!foundToday) {
                foundToday = true;
                status = completedToday ? ChapterViewStatus.TODAY_LOCKED : ChapterViewStatus.TODAY;
            } else {
                status = ChapterViewStatus.LOCKED;
            }

            chapterInfos.add(StorybookConverter.toChapterInfo(sc, status));
        }

        return StorybookConverter.toStorybookDetail(storybook, chapterInfos);
    }

    @Transactional
    public StorybookResDTO.StartStorybook startStorybook(Long storybookId, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        Storybook storybook = storybookRepository.findById(storybookId)
                .orElseThrow(() -> new StorybookException(StorybookErrorCode.NOT_FOUND));

        Optional<MemberStorybook> existing =
                memberStorybookRepository.findByMemberAndStorybook(member, storybook);

        if (existing.isPresent()) {
            if (isStorybookCompleted(member, storybook)) {
                throw new StorybookException(StorybookErrorCode.ALREADY_COMPLETED);
            } else {
                throw new StorybookException(StorybookErrorCode.ALREADY_IN_PROGRESS);
            }
        }

        MemberStorybook memberStorybook = MemberStorybook.builder()
                .member(member)
                .storybook(storybook)
                .lastChapterOrder(1)
                .build();

        try {
            memberStorybookRepository.save(memberStorybook);
        } catch (DataIntegrityViolationException e) {
            throw new StorybookException(StorybookErrorCode.ALREADY_IN_PROGRESS);
        }

        return StorybookConverter.toStartStorybook(memberStorybook, storybook);
    }

    public StorybookResDTO.GetStorybookList getStorybookList(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        List<Storybook> storybooks = storybookRepository.findAll().stream()
                .sorted(Comparator.comparing(Storybook::getThemeOrder))
                .toList();

        Map<Long, MemberStorybook> memberStorybookMap = memberStorybookRepository.findByMember(member).stream()
                .collect(Collectors.toMap(ms -> ms.getStorybook().getId(), Function.identity()));

        List<StorybookResDTO.StorybookSummary> storybookSummaries = storybooks.stream()
                .map(storybook -> buildStorybookSummary(member, storybook, memberStorybookMap.get(storybook.getId())))
                .toList();

        List<StorybookResDTO.SituationalRecommendation> situationalRecommendations =
                buildSituationalRecommendations();

        return StorybookConverter.toStorybookList(storybookSummaries, situationalRecommendations);
    }

    public StorybookResDTO.GetRecommendedStorybooks getRecommendedStorybooks(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        List<Tag> desiredTags = memberTagRepository.findByMemberAndTag_Category(member, Category.DESIRED_DIRECTION).stream()
                .map(MemberTag::getTag)
                .toList();

        List<StorybookTag> matchedStorybookTags = desiredTags.isEmpty()
                ? List.of()
                : storybookTagRepository.findByTagIn(desiredTags);

        Map<Long, StorybookTag> bestMatchByStorybook = new LinkedHashMap<>();
        for (StorybookTag st : matchedStorybookTags) {
            Long storybookId = st.getStorybook().getId();
            StorybookTag existing = bestMatchByStorybook.get(storybookId);
            if (existing == null || (existing.getPriorityLevel() == PriorityLevel.SECONDARY
                    && st.getPriorityLevel() == PriorityLevel.PRIMARY)) {
                bestMatchByStorybook.put(storybookId, st);
            }
        }

        List<StorybookResDTO.RecommendedStorybook> recommendations = bestMatchByStorybook.values().stream()
                .sorted(Comparator.comparing(StorybookTag::getPriorityLevel))
                .limit(2)
                .map(StorybookConverter::toRecommendedStorybook)
                .collect(Collectors.toCollection(ArrayList::new));

        if (recommendations.size() < 2) {
            Set<Long> alreadyIncluded = recommendations.stream()
                    .map(StorybookResDTO.RecommendedStorybook::storybookId)
                    .collect(Collectors.toSet());

            List<Storybook> fallbackStorybooks = storybookRepository.findAll().stream()
                    .sorted(Comparator.comparing(Storybook::getThemeOrder))
                    .filter(sb -> !alreadyIncluded.contains(sb.getId()))
                    .toList();

            for (Storybook sb : fallbackStorybooks) {
                if (recommendations.size() >= 2) break;
                recommendations.add(StorybookConverter.toRecommendedStorybook(sb, sb.getShortDescription()));
            }
        }

        return StorybookConverter.toRecommendedStorybooksResult(recommendations);
    }

    public StorybookResDTO.GetHome getHome(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        List<Storybook> storybooks = storybookRepository.findAll().stream()
                .sorted(Comparator.comparing(Storybook::getThemeOrder))
                .toList();

        Map<Long, MemberStorybook> memberStorybookMap = memberStorybookRepository.findByMember(member).stream()
                .collect(Collectors.toMap(ms -> ms.getStorybook().getId(), Function.identity()));

        // 스토리북마다 상태 계산 (목록 조회와 동일한 로직)
        Map<Storybook, StorybookStatus> statusMap = new LinkedHashMap<>();
        for (Storybook sb : storybooks) {
            MemberStorybook ms = memberStorybookMap.get(sb.getId());
            if (ms == null) {
                statusMap.put(sb, StorybookStatus.NOT_STARTED);
                continue;
            }

            List<MemberChapter> memberChapters =
                    memberChapterRepository.findByMemberAndStorybookChapter_Storybook_Id(member, sb.getId());

            boolean completed = isCompleted(sb, memberChapters);
            boolean completedToday = ms.isCompletedToday();

            StorybookStatus status = completed ? StorybookStatus.COMPLETED
                    : completedToday ? StorybookStatus.TODAY_DONE
                    : StorybookStatus.IN_PROGRESS;
            statusMap.put(sb, status);
        }

        // 완료 안 하고 시작은 한 스토리북들(진행중 + 오늘 완료) 추리기
        List<Storybook> activeStorybooks = statusMap.entrySet().stream()
                .filter(e -> e.getValue() == StorybookStatus.IN_PROGRESS || e.getValue() == StorybookStatus.TODAY_DONE)
                .map(Map.Entry::getKey)
                .sorted(Comparator
                        .comparing((Storybook sb) -> statusMap.get(sb) != StorybookStatus.IN_PROGRESS)
                        .thenComparing(Storybook::getThemeOrder))
                .toList();

        HomeStatus homeStatus;
        StorybookResDTO.RepresentativeStorybook representativeStorybook = null;
        int otherInProgressCount = 0;

        if (activeStorybooks.isEmpty()) {
            homeStatus = HomeStatus.NO_STORYBOOK;
        } else {
            Storybook representative = activeStorybooks.get(0);
            StorybookStatus repStatus = statusMap.get(representative);
            MemberStorybook repMemberStorybook = memberStorybookMap.get(representative.getId());

            Integer chapterOrder = repMemberStorybook.getLastChapterOrder();
            String chapterTitle = storybookChapterRepository
                    .findByStorybook_IdAndChapterOrder(representative.getId(), chapterOrder)
                    .map(sc -> sc.getChapter().getTitle())
                    .orElse(null);

            representativeStorybook = StorybookConverter.toRepresentativeStorybook(
                    representative, chapterTitle, chapterOrder, repStatus == StorybookStatus.IN_PROGRESS);

            otherInProgressCount = activeStorybooks.size() - 1;

            boolean allTodayDone = activeStorybooks.stream()
                    .allMatch(sb -> statusMap.get(sb) == StorybookStatus.TODAY_DONE);

            if (allTodayDone) {
                homeStatus = HomeStatus.ALL_COMPLETED_TODAY;
            } else if (activeStorybooks.size() == 1) {
                homeStatus = HomeStatus.IN_PROGRESS;
            } else {
                homeStatus = HomeStatus.MULTIPLE_IN_PROGRESS;
            }
        }

        List<StorybookResDTO.BookshelfItem> bookshelf = storybooks.stream()
                .map(sb -> StorybookConverter.toBookshelfItem(sb, toBookshelfStatus(statusMap.get(sb))))
                .toList();

        List<StorybookResDTO.RecommendedStorybook> recommendedStorybooks = new ArrayList<>();
        if (activeStorybooks.isEmpty()) {
            StorybookResDTO.GetRecommendedStorybooks recommended = getRecommendedStorybooks(memberId);
            recommendedStorybooks = recommended.storybooks().stream()
                    .map(r -> StorybookConverter.toRecommendedStorybook(r, member.getName() + "님을 위한 추천 스토리북"))
                    .toList();
        }

        return StorybookConverter.toHome(
                homeStatus,
                member.getName() + "님",
                representativeStorybook,
                otherInProgressCount,
                bookshelf,
                recommendedStorybooks
        );
    }

    private BookshelfStatus toBookshelfStatus(StorybookStatus status) {
        return switch (status) {
            case COMPLETED -> BookshelfStatus.COMPLETED;
            case NOT_STARTED -> BookshelfStatus.NOT_STARTED;
            case IN_PROGRESS, TODAY_DONE -> BookshelfStatus.IN_PROGRESS;
        };
    }

    private StorybookResDTO.StorybookSummary buildStorybookSummary(
            Member member, Storybook storybook, MemberStorybook memberStorybook) {

        if (memberStorybook == null) {
            return StorybookConverter.toStorybookSummary(storybook, StorybookStatus.NOT_STARTED, null);
        }

        List<MemberChapter> memberChapters =
                memberChapterRepository.findByMemberAndStorybookChapter_Storybook_Id(member, storybook.getId());

        boolean completed = isCompleted(storybook, memberChapters);

        boolean completedToday = memberStorybook.isCompletedToday();

        StorybookStatus status;
        if (completed) {
            status = StorybookStatus.COMPLETED;
        } else if (completedToday) {
            status = StorybookStatus.TODAY_DONE;
        } else {
            status = StorybookStatus.IN_PROGRESS;
        }

        return StorybookConverter.toStorybookSummary(storybook, status, memberStorybook);
    }

    private boolean isStorybookCompleted(Member member, Storybook storybook) {
        List<MemberChapter> memberChapters =
                memberChapterRepository.findByMemberAndStorybookChapter_Storybook_Id(member, storybook.getId());
        return isCompleted(storybook, memberChapters);
    }

    private boolean isCompleted(Storybook storybook, List<MemberChapter> memberChapters) {
        int totalChapters =
                storybookChapterRepository.findByStorybook_IdOrderByChapterOrderAsc(storybook.getId()).size();

        long completedCount = memberChapters.stream()
                .filter(mc -> mc.getStatus() == Status.COMPLETED)
                .count();

        return completedCount == totalChapters;
    }

    private List<StorybookResDTO.SituationalRecommendation> buildSituationalRecommendations() {

        List<Tag> desiredDirectionTags = tagRepository.findByCategory(Category.DESIRED_DIRECTION);

        // 태그마다 따로 조회하지 않고 한 번에 배치 조회 후 그룹핑 (N+1 방지)
        List<StorybookTag> primaryStorybookTags =
                storybookTagRepository.findByTagInAndPriorityLevel(desiredDirectionTags, PriorityLevel.PRIMARY);

        Map<Tag, List<StorybookTag>> storybookTagsByTag = primaryStorybookTags.stream()
                .collect(Collectors.groupingBy(StorybookTag::getTag));

        return desiredDirectionTags.stream()
                .map(tag -> {
                    List<StorybookResDTO.SituationalStorybook> situationalStorybooks =
                            storybookTagsByTag.getOrDefault(tag, List.of()).stream()
                                    .map(StorybookConverter::toSituationalStorybook)
                                    .toList();

                    return StorybookConverter.toSituationalRecommendation(tag.getTitle(), situationalStorybooks);
                })
                .toList();
    }
}
