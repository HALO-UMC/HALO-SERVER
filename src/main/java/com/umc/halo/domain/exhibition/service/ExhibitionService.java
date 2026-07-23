package com.umc.halo.domain.exhibition.service;

import com.umc.halo.domain.content.storybook.entity.Storybook;
import com.umc.halo.domain.content.storybook.entity.StorybookCharacter;
import com.umc.halo.domain.content.storybook.enums.Variant;
import com.umc.halo.domain.content.storybook.exception.StorybookException;
import com.umc.halo.domain.content.storybook.exception.code.StorybookErrorCode;
import com.umc.halo.domain.content.storybook.repository.StorybookChapterRepository;
import com.umc.halo.domain.content.storybook.repository.StorybookCharacterRepository;
import com.umc.halo.domain.content.storybook.service.StorybookService;
import com.umc.halo.domain.exhibition.converter.ExhibitionConverter;
import com.umc.halo.domain.exhibition.dto.ExhibitionResDTO;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.exception.MemberException;
import com.umc.halo.domain.member.exception.code.MemberErrorCode;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.record.entity.MemberStorybook;
import com.umc.halo.domain.record.enums.Status;
import com.umc.halo.domain.record.repository.MemberChapterRepository;
import com.umc.halo.domain.record.repository.MemberStorybookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExhibitionService {

    private final MemberRepository memberRepository;
    private final MemberStorybookRepository memberStorybookRepository;
    private final MemberChapterRepository memberChapterRepository;
    private final StorybookChapterRepository storybookChapterRepository;
    private final StorybookCharacterRepository storybookCharacterRepository;
    private final StorybookService storybookService; 
    public ExhibitionResDTO.MainInfo getExhibition(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        List<MemberStorybook> memberStorybooks = memberStorybookRepository.findByMember(member);

        List<MemberStorybook> completed = new ArrayList<>();
        List<MemberStorybook> inProgress = new ArrayList<>();
        for (MemberStorybook ms : memberStorybooks) {
            if (isCompleted(member, ms.getStorybook())) {
                completed.add(ms);
            } else {
                inProgress.add(ms);
            }
        }
        int collectedCharacterCount = completed.size();
        int inProgressStorybookCount = inProgress.size();
        List<ExhibitionResDTO.CompletedStorybook> completedDtos = new ArrayList<>();
        List<ExhibitionResDTO.InProgressStorybook> inProgressDtos = new ArrayList<>();
        List<ExhibitionResDTO.RecommendedStorybook> recommendedDtos = new ArrayList<>();
        Long currentStorybookId = null;

        if (!completed.isEmpty()) {
            completed.sort(Comparator.comparing(MemberStorybook::getLastCompletedDate).reversed());
            currentStorybookId = completed.get(0).getStorybook().getId();

            for (MemberStorybook ms : completed) {
                StorybookCharacter character = storybookCharacterRepository
                        .findByStorybookAndVariant(ms.getStorybook(), Variant.ORIGINAL)
                        .orElseThrow(() -> new StorybookException(StorybookErrorCode.NOT_FOUND_CHARACTER));
                completedDtos.add(ExhibitionConverter.toCompletedStorybook(ms, character));
            }
        } else if (!inProgress.isEmpty()) {
            for (MemberStorybook ms : inProgress) {
                inProgressDtos.add(ExhibitionConverter.toInProgressStorybook(ms));
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
    private boolean isCompleted(Member member, Storybook storybook) {
        int totalChapters = storybookChapterRepository
                .findByStorybook_IdOrderByChapterOrderAsc(storybook.getId()).size();

        long completedCount = memberChapterRepository
                .findByMemberAndStorybookChapter_Storybook_Id(member, storybook.getId()).stream()
                .filter(mc -> mc.getStatus() == Status.COMPLETED)
                .count();

        return totalChapters > 0 && completedCount == totalChapters;
    }
}