package com.umc.halo.domain.content.storybook.service;

import com.umc.halo.domain.content.storybook.apiPayload.StorybookErrorCode;
import com.umc.halo.domain.content.storybook.dto.response.StorybookDetailResponse;
import com.umc.halo.domain.content.storybook.dto.response.StorybookListResponse;
import com.umc.halo.domain.content.storybook.dto.response.StorybookStartResponse;
import com.umc.halo.domain.content.storybook.entity.Storybook;
import com.umc.halo.domain.content.storybook.entity.StorybookChapter;
import com.umc.halo.domain.content.storybook.enums.ChapterViewStatus;
import com.umc.halo.domain.content.storybook.enums.StorybookStatus;
import com.umc.halo.domain.content.storybook.repository.StorybookChapterRepository;
import com.umc.halo.domain.content.storybook.repository.StorybookRepository;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.exception.code.MemberErrorCode;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.record.entity.MemberChapter;
import com.umc.halo.domain.record.entity.MemberStorybook;
import com.umc.halo.domain.record.enums.Status;
import com.umc.halo.domain.record.repository.MemberChapterRepository;
import com.umc.halo.domain.record.repository.MemberStorybookRepository;
import com.umc.halo.domain.tag.entity.StorybookTag;
import com.umc.halo.domain.tag.entity.Tag;
import com.umc.halo.domain.tag.enums.Category;
import com.umc.halo.domain.tag.enums.PriorityLevel;
import com.umc.halo.domain.tag.repository.StorybookTagRepository;
import com.umc.halo.domain.tag.repository.TagRepository;
import com.umc.halo.global.apiPayload.exception.ProjectException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
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

    public StorybookDetailResponse.GetStorybookDetail getStorybookDetail(Long storybookId, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ProjectException(MemberErrorCode.NOT_FOUND));

        Storybook storybook = storybookRepository.findById(storybookId)
                .orElseThrow(() -> new ProjectException(StorybookErrorCode.NOT_FOUND));

        List<StorybookChapter> storybookChapters =
                storybookChapterRepository.findByStorybook_IdOrderByChapterOrderAsc(storybookId);

        List<MemberChapter> memberChapters =
                memberChapterRepository.findByMemberAndStorybookChapter_Storybook_Id(member, storybookId);

        Set<Long> completedChapterIds = memberChapters.stream()
                .filter(mc -> mc.getStatus() == Status.COMPLETED)
                .map(mc -> mc.getStorybookChapter().getId())
                .collect(Collectors.toSet());

        boolean completedToday = memberChapters.stream()
                .anyMatch(mc -> mc.getStatus() == Status.COMPLETED
                        && mc.getCompletedDate() != null
                        && mc.getCompletedDate().isEqual(LocalDate.now()));

        boolean foundToday = false;
        List<StorybookDetailResponse.ChapterInfo> chapterInfos = new ArrayList<>();

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

            chapterInfos.add(new StorybookDetailResponse.ChapterInfo(
                    sc.getChapterOrder(),
                    sc.getChapter().getTitle(),
                    sc.getChapter().getImageUrl(),
                    sc.getChapter().getShortDescription(),
                    sc.getChapter().getDescription(),
                    status
            ));
        }

        return new StorybookDetailResponse.GetStorybookDetail(
                storybook.getId(),
                storybook.getTitle(),
                storybook.getDescription(),
                storybook.getImageUrl(),
                chapterInfos
        );
    }

    @Transactional
    public StorybookStartResponse.StartStorybook startStorybook(Long storybookId, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ProjectException(MemberErrorCode.NOT_FOUND));

        Storybook storybook = storybookRepository.findById(storybookId)
                .orElseThrow(() -> new ProjectException(StorybookErrorCode.NOT_FOUND));

        Optional<MemberStorybook> existing =
                memberStorybookRepository.findByMemberAndStorybook(member, storybook);

        if (existing.isPresent()) {
            if (isStorybookCompleted(member, storybook)) {
                throw new ProjectException(StorybookErrorCode.ALREADY_COMPLETED);
            } else {
                throw new ProjectException(StorybookErrorCode.ALREADY_IN_PROGRESS);
            }
        }

        MemberStorybook memberStorybook = MemberStorybook.builder()
                .member(member)
                .storybook(storybook)
                .lastChapterOrder(1)
                .build();

        memberStorybookRepository.save(memberStorybook);

        return new StorybookStartResponse.StartStorybook(
                memberStorybook.getId(),
                storybook.getId(),
                StorybookStatus.IN_PROGRESS
        );
    }

    public StorybookListResponse.GetStorybookList getStorybookList(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ProjectException(MemberErrorCode.NOT_FOUND));

        List<Storybook> storybooks = storybookRepository.findAll().stream()
                .sorted(Comparator.comparing(Storybook::getThemeOrder))
                .toList();

        Map<Long, MemberStorybook> memberStorybookMap = memberStorybookRepository.findByMember(member).stream()
                .collect(Collectors.toMap(ms -> ms.getStorybook().getId(), Function.identity()));

        List<StorybookListResponse.StorybookSummary> storybookSummaries = storybooks.stream()
                .map(storybook -> buildStorybookSummary(member, storybook, memberStorybookMap.get(storybook.getId())))
                .toList();

        List<StorybookListResponse.SituationalRecommendation> situationalRecommendations =
                buildSituationalRecommendations();

        return new StorybookListResponse.GetStorybookList(storybookSummaries, situationalRecommendations);
    }

    private StorybookListResponse.StorybookSummary buildStorybookSummary(
            Member member, Storybook storybook, MemberStorybook memberStorybook) {

        if (memberStorybook == null) {
            return new StorybookListResponse.StorybookSummary(
                    storybook.getId(),
                    storybook.getTitle(),
                    storybook.getThemeOrder(),
                    storybook.getShortDescription(),
                    storybook.getImageUrl(),
                    StorybookStatus.NOT_STARTED,
                    null,
                    null
            );
        }

        List<MemberChapter> memberChapters =
                memberChapterRepository.findByMemberAndStorybookChapter_Storybook_Id(member, storybook.getId());

        boolean completed = isCompleted(storybook, memberChapters);

        boolean completedToday = memberChapters.stream()
                .anyMatch(mc -> mc.getStatus() == Status.COMPLETED
                        && mc.getCompletedDate() != null
                        && mc.getCompletedDate().isEqual(LocalDate.now()));

        StorybookStatus status;
        if (completed) {
            status = StorybookStatus.COMPLETED;
        } else if (completedToday) {
            status = StorybookStatus.TODAY_DONE;
        } else {
            status = StorybookStatus.IN_PROGRESS;
        }

        return new StorybookListResponse.StorybookSummary(
                storybook.getId(),
                storybook.getTitle(),
                storybook.getThemeOrder(),
                storybook.getShortDescription(),
                storybook.getImageUrl(),
                status,
                memberStorybook.getLastChapterOrder(),
                memberStorybook.getLastCompletedDate()
        );
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

    private List<StorybookListResponse.SituationalRecommendation> buildSituationalRecommendations() {

        List<Tag> desiredDirectionTags = tagRepository.findByCategory(Category.DESIRED_DIRECTION);

        return desiredDirectionTags.stream()
                .map(tag -> {
                    List<StorybookTag> primaryStorybookTags =
                            storybookTagRepository.findByTagAndPriorityLevel(tag, PriorityLevel.PRIMARY);

                    List<StorybookListResponse.RecommendedStorybook> recommendedStorybooks = primaryStorybookTags.stream()
                            .map(st -> new StorybookListResponse.RecommendedStorybook(
                                    st.getStorybook().getId(),
                                    st.getStorybook().getTitle(),
                                    st.getStorybook().getImageUrl(),
                                    st.getPhrase()
                            ))
                            .toList();

                    return new StorybookListResponse.SituationalRecommendation(tag.getTitle(), recommendedStorybooks);
                })
                .toList();
    }
}