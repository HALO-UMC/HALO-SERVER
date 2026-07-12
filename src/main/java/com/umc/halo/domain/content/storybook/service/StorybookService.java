package com.umc.halo.domain.content.storybook.service;

import com.umc.halo.domain.content.storybook.apiPayload.StorybookErrorCode;
import com.umc.halo.domain.content.storybook.dto.response.StorybookDetailResponse;
import com.umc.halo.domain.content.storybook.dto.response.StorybookStartResponse;
import com.umc.halo.domain.content.storybook.entity.Storybook;
import com.umc.halo.domain.content.storybook.entity.StorybookChapter;
import com.umc.halo.domain.content.storybook.enums.ChapterViewStatus;
import com.umc.halo.domain.content.storybook.repository.StorybookChapterRepository;
import com.umc.halo.domain.content.storybook.repository.StorybookRepository;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.record.entity.MemberChapter;
import com.umc.halo.domain.record.entity.MemberStorybook;
import com.umc.halo.domain.record.enums.Status;
import com.umc.halo.domain.record.repository.MemberChapterRepository;
import com.umc.halo.domain.record.repository.MemberStorybookRepository;
import com.umc.halo.global.apiPayload.code.GeneralErrorCode;
import com.umc.halo.global.apiPayload.exception.ProjectException;
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
    private final MemberRepository memberRepository;
    private final MemberStorybookRepository memberStorybookRepository;

    public StorybookDetailResponse.GetStorybookDetail getStorybookDetail(Long storybookId, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ProjectException(GeneralErrorCode.NOT_FOUND));

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
                .orElseThrow(() -> new ProjectException(GeneralErrorCode.NOT_FOUND));

        Storybook storybook = storybookRepository.findById(storybookId)
                .orElseThrow(() -> new ProjectException(StorybookErrorCode.NOT_FOUND));

        if (memberStorybookRepository.existsByMemberAndStorybook(member, storybook)) {
            throw new ProjectException(StorybookErrorCode.ALREADY_STARTED);
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
                memberStorybook.getLastChapterOrder()
        );
    }
}