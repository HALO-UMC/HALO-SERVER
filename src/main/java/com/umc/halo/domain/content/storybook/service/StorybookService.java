package com.umc.halo.domain.content.storybook.service;

import com.umc.halo.domain.content.chapter.entity.Chapter;
import com.umc.halo.domain.content.chapter.repository.ChapterRepository;
import com.umc.halo.domain.content.storybook.converter.StorybookConverter;
import com.umc.halo.domain.content.storybook.dto.StorybookResDTO;
import com.umc.halo.domain.content.storybook.entity.Storybook;
import com.umc.halo.domain.content.storybook.enums.ChapterViewStatus;
import com.umc.halo.domain.content.storybook.enums.HomeStatus;
import com.umc.halo.domain.content.storybook.enums.StorybookStatus;
import com.umc.halo.domain.content.storybook.exception.StorybookException;
import com.umc.halo.domain.content.storybook.exception.code.StorybookErrorCode;
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

import java.time.LocalDate;
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
    private final ChapterRepository chapterRepository;
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

        List<Chapter> Chapters =
                chapterRepository.findByStorybook_IdOrderByChapterOrderAsc(storybookId);

        List<MemberChapter> memberChapters =
                memberChapterRepository.findByMemberAndChapter_Storybook_Id(member, storybookId);

        Optional<MemberStorybook> memberStorybookOpt =
                memberStorybookRepository.findByMemberAndStorybook(member, storybook);

        Map<Long, Long> completedChapterIdToMemberChapterId = memberChapters.stream()
                .filter(mc -> mc.getStatus() == Status.COMPLETED)
                .collect(Collectors.toMap(mc -> mc.getChapter().getId(), MemberChapter::getId, (a, b) -> b));

        boolean completedToday = memberStorybookOpt
                .map(MemberStorybook::isCompletedToday)
                .orElse(false);

        boolean foundToday = false;
        List<StorybookResDTO.ChapterInfo> chapterInfos = new ArrayList<>();

        for (Chapter sc : Chapters) {
            Long memberChapterId = completedChapterIdToMemberChapterId.get(sc.getId());
            ChapterViewStatus status;
            if (memberChapterId != null) {
                status = ChapterViewStatus.COMPLETED;
            } else if (!foundToday) {
                foundToday = true;
                status = completedToday ? ChapterViewStatus.TODAY_LOCKED : ChapterViewStatus.TODAY;
            } else {
                status = ChapterViewStatus.LOCKED;
            }

            chapterInfos.add(StorybookConverter.toChapterInfo(sc, status, memberChapterId));
        }
        int completedChapterCount = completedChapterIdToMemberChapterId.size();
        int progressPercentage = completedChapterCount * 10;

        return StorybookConverter.toStorybookDetail(storybook, chapterInfos, completedChapterCount, progressPercentage);
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
                .startedDate(LocalDate.now())
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
                .sorted(Comparator.comparing(Storybook::getId))
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

        Set<Long> startedStorybookIds = memberStorybookRepository.findByMember(member).stream()
                .map(ms -> ms.getStorybook().getId())
                .collect(Collectors.toSet());

        List<Tag> desiredTags = memberTagRepository.findByMemberAndTag_Category(member, Category.DESIRED_DIRECTION).stream()
                .map(MemberTag::getTag)
                .toList();

        List<StorybookTag> matchedStorybookTags = desiredTags.isEmpty()
                ? List.of()
                : storybookTagRepository.findByTagIn(desiredTags);

        Map<Long, StorybookTag> bestMatchByStorybook = new LinkedHashMap<>();
        for (StorybookTag st : matchedStorybookTags) {
            Long storybookId = st.getStorybook().getId();
            if (startedStorybookIds.contains(storybookId)) {
                continue;
            }
            StorybookTag existing = bestMatchByStorybook.get(storybookId);
            if (existing == null || (existing.getPriorityLevel() == PriorityLevel.SECONDARY
                    && st.getPriorityLevel() == PriorityLevel.PRIMARY)) {
                bestMatchByStorybook.put(storybookId, st);
            }
        }

        List<StorybookResDTO.RecommendedStorybook> recommendations = bestMatchByStorybook.values().stream()
                .sorted(Comparator.comparing(StorybookTag::getPriorityLevel))
                .limit(2)
                .map(st -> StorybookConverter.toRecommendedStorybook(st, st.getStorybook().getRecommendationPhrase()))
                .collect(Collectors.toCollection(ArrayList::new));

        if (recommendations.size() < 2) {
            Set<Long> alreadyIncluded = recommendations.stream()
                    .map(StorybookResDTO.RecommendedStorybook::storybookId)
                    .collect(Collectors.toSet());

            List<Storybook> fallbackStorybooks = storybookRepository.findAll().stream()
                    .sorted(Comparator.comparing(Storybook::getId))
                    .filter(sb -> !alreadyIncluded.contains(sb.getId()))
                    .filter(sb -> !startedStorybookIds.contains(sb.getId()))
                    .toList();

            for (Storybook sb : fallbackStorybooks) {
                if (recommendations.size() >= 2) break;
                recommendations.add(StorybookConverter.toRecommendedStorybook(sb, sb.getRecommendationPhrase()));
            }
        }

        return StorybookConverter.toRecommendedStorybooksResult(recommendations);
    }

    public StorybookResDTO.GetHome getHome(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        List<Storybook> storybooks = storybookRepository.findAll().stream()
                .sorted(Comparator.comparing(Storybook::getId))
                .toList();

        Map<Long, MemberStorybook> memberStorybookMap = memberStorybookRepository.findByMember(member).stream()
                .collect(Collectors.toMap(ms -> ms.getStorybook().getId(), Function.identity()));

        // 스토리북마다 상태 계산 (목록 조회와 동일한 로직)
        Map<Storybook, StorybookStatus> statusMap = new LinkedHashMap<>();
        Map<Long, List<MemberChapter>> memberChaptersMap = new LinkedHashMap<>();
        for (Storybook sb : storybooks) {
            MemberStorybook ms = memberStorybookMap.get(sb.getId());
            if (ms == null) {
                statusMap.put(sb, StorybookStatus.NOT_STARTED);
                continue;
            }

            List<MemberChapter> memberChapters =
                    memberChapterRepository.findByMemberAndChapter_Storybook_Id(member, sb.getId());
            memberChaptersMap.put(sb.getId(), memberChapters);

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
                        .thenComparing(Storybook::getId))
                .toList();

        HomeStatus homeStatus;
        List<StorybookResDTO.InProgressStorybook> inProgressStorybooks = new ArrayList<>();

        if (activeStorybooks.isEmpty()) {
            homeStatus = HomeStatus.NO_STORYBOOK;
        } else {
            boolean allTodayDone = activeStorybooks.stream()
                    .allMatch(sb -> statusMap.get(sb) == StorybookStatus.TODAY_DONE);

            homeStatus = allTodayDone ? HomeStatus.ALL_COMPLETED_TODAY
                    : activeStorybooks.size() == 1 ? HomeStatus.IN_PROGRESS
                    : HomeStatus.MULTIPLE_IN_PROGRESS;

            for (Storybook sb : activeStorybooks) {
                MemberStorybook memberStorybook = memberStorybookMap.get(sb.getId());
                Integer chapterOrder = memberStorybook.resolveDisplayChapterOrder(memberChaptersMap.get(sb.getId()));
                boolean todayAvailable = statusMap.get(sb) == StorybookStatus.IN_PROGRESS;
                inProgressStorybooks.add(StorybookConverter.toInProgressStorybook(sb, chapterOrder, todayAvailable));
            }
        }

        List<StorybookResDTO.RecommendedStorybook> recommendedStorybooks = new ArrayList<>();
        if (activeStorybooks.isEmpty()) {
            recommendedStorybooks = getRecommendedStorybooks(memberId).storybooks();
        }

        return StorybookConverter.toHome(
                homeStatus,
                member.getName() + "님",
                inProgressStorybooks,
                recommendedStorybooks
        );
    }

    private StorybookResDTO.StorybookSummary buildStorybookSummary(
            Member member, Storybook storybook, MemberStorybook memberStorybook) {

        if (memberStorybook == null) {
            return StorybookConverter.toStorybookSummary(storybook, StorybookStatus.NOT_STARTED, null, null);
        }

        List<MemberChapter> memberChapters =
                memberChapterRepository.findByMemberAndChapter_Storybook_Id(member, storybook.getId());

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

        Integer displayChapterOrder = memberStorybook.resolveDisplayChapterOrder(memberChapters);
        return StorybookConverter.toStorybookSummary(storybook, status, memberStorybook, displayChapterOrder);
    }

    private boolean isStorybookCompleted(Member member, Storybook storybook) {
        List<MemberChapter> memberChapters =
                memberChapterRepository.findByMemberAndChapter_Storybook_Id(member, storybook.getId());
        return isCompleted(storybook, memberChapters);
    }

    private boolean isCompleted(Storybook storybook, List<MemberChapter> memberChapters) {
        int totalChapters =
                chapterRepository.findByStorybook_IdOrderByChapterOrderAsc(storybook.getId()).size();

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
                                    .limit(2)
                                    .map(st -> StorybookConverter.toSituationalStorybook(st, st.getStorybook().getRecommendationPhrase()))
                                    .toList();

                    return StorybookConverter.toSituationalRecommendation(tag.getTitle(), situationalStorybooks);
                })
                .toList();
    }
}
