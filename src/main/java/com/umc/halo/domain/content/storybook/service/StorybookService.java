package com.umc.halo.domain.content.storybook.service;

import com.umc.halo.domain.content.storybook.dto.response.StorybookDetailResponse;
import com.umc.halo.domain.content.storybook.entity.Storybook;
import com.umc.halo.domain.content.storybook.entity.StorybookChapter;
import com.umc.halo.domain.content.storybook.enums.ChapterViewStatus;
import com.umc.halo.domain.content.storybook.repository.StorybookChapterRepository;
import com.umc.halo.domain.content.storybook.repository.StorybookRepository;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.record.entity.MemberChapter;
import com.umc.halo.domain.record.enums.Status;
import com.umc.halo.domain.record.repository.MemberChapterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StorybookService {

    private final StorybookRepository storybookRepository;
    private final StorybookChapterRepository storybookChapterRepository;
    private final MemberChapterRepository memberChapterRepository;

    public StorybookDetailResponse.GetStorybookDetail getStorybookDetail(Long storybookId, Member member) {

        Storybook storybook = storybookRepository.findById(storybookId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스토리북입니다."));

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
}