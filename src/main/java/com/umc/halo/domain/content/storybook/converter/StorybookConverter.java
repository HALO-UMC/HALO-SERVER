package com.umc.halo.domain.content.storybook.converter;

import com.umc.halo.domain.content.storybook.dto.StorybookResDTO;
import com.umc.halo.domain.content.storybook.entity.Storybook;
import com.umc.halo.domain.content.storybook.entity.StorybookChapter;
import com.umc.halo.domain.content.storybook.enums.BookshelfStatus;
import com.umc.halo.domain.content.storybook.enums.ChapterViewStatus;
import com.umc.halo.domain.content.storybook.enums.HomeStatus;
import com.umc.halo.domain.content.storybook.enums.StorybookStatus;
import com.umc.halo.domain.record.entity.MemberStorybook;
import com.umc.halo.domain.tag.entity.StorybookTag;

import java.util.List;

public class StorybookConverter {

    private StorybookConverter() {
        throw new IllegalStateException("유틸리티 클래스는 인스턴스화할 수 없습니다.");
    }

    public static StorybookResDTO.ChapterInfo toChapterInfo(StorybookChapter sc, ChapterViewStatus status) {
        return StorybookResDTO.ChapterInfo.builder()
                .chapterOrder(sc.getChapterOrder())
                .title(sc.getChapter().getTitle())
                .imageUrl(sc.getChapter().getImageUrl())
                .shortDescription(sc.getChapter().getShortDescription())
                .description(sc.getChapter().getDescription())
                .status(status)
                .build();
    }

    public static StorybookResDTO.GetStorybookDetail toStorybookDetail(
            Storybook storybook, List<StorybookResDTO.ChapterInfo> chapterInfos,
            int completedChapterCount, int progressPercentage) {
        return StorybookResDTO.GetStorybookDetail.builder()
                .storybookId(storybook.getId())
                .title(storybook.getTitle())
                .description(storybook.getDescription())
                .imageUrl(storybook.getImageUrl())
                .completedChapterCount(completedChapterCount)
                .progressPercentage(progressPercentage)
                .chapters(chapterInfos)
                .build();
    }

    public static StorybookResDTO.StartStorybook toStartStorybook(MemberStorybook memberStorybook, Storybook storybook) {
        return StorybookResDTO.StartStorybook.builder()
                .memberStorybookId(memberStorybook.getId())
                .storybookId(storybook.getId())
                .status(StorybookStatus.IN_PROGRESS)
                .build();
    }

    public static StorybookResDTO.StorybookSummary toStorybookSummary(
            Storybook storybook, StorybookStatus status, MemberStorybook memberStorybook) {
        return StorybookResDTO.StorybookSummary.builder()
                .storybookId(storybook.getId())
                .title(storybook.getTitle())
                .themeOrder(storybook.getThemeOrder())
                .shortDescription(storybook.getShortDescription())
                .imageUrl(storybook.getImageUrl())
                .status(status)
                .lastChapterOrder(memberStorybook == null ? null : memberStorybook.getLastChapterOrder())
                .lastCompletedDate(memberStorybook == null ? null : memberStorybook.getLastCompletedDate())
                .build();
    }

    public static StorybookResDTO.GetStorybookList toStorybookList(
            List<StorybookResDTO.StorybookSummary> summaries,
            List<StorybookResDTO.SituationalRecommendation> recommendations) {
        return StorybookResDTO.GetStorybookList.builder()
                .storybooks(summaries)
                .situationalRecommendations(recommendations)
                .build();
    }

    public static StorybookResDTO.SituationalStorybook toSituationalStorybook(StorybookTag st) {
        return StorybookResDTO.SituationalStorybook.builder()
                .storybookId(st.getStorybook().getId())
                .title(st.getStorybook().getTitle())
                .imageUrl(st.getStorybook().getImageUrl())
                .recommendationReasonText(st.getPhrase())
                .build();
    }

    public static StorybookResDTO.SituationalRecommendation toSituationalRecommendation(
            String tag, List<StorybookResDTO.SituationalStorybook> storybooks) {
        return StorybookResDTO.SituationalRecommendation.builder()
                .tag(tag)
                .storybooks(storybooks)
                .build();
    }

    public static StorybookResDTO.RecommendedStorybook toRecommendedStorybook(StorybookTag st) {
        return StorybookResDTO.RecommendedStorybook.builder()
                .storybookId(st.getStorybook().getId())
                .title(st.getStorybook().getTitle())
                .shortDescription(st.getStorybook().getShortDescription())
                .imageUrl(st.getStorybook().getImageUrl())
                .recommendationReasonText(st.getPhrase())
                .build();
    }

    public static StorybookResDTO.RecommendedStorybook toRecommendedStorybook(Storybook sb, String reasonText) {
        return StorybookResDTO.RecommendedStorybook.builder()
                .storybookId(sb.getId())
                .title(sb.getTitle())
                .shortDescription(sb.getShortDescription())
                .imageUrl(sb.getImageUrl())
                .recommendationReasonText(reasonText)
                .build();
    }

    public static StorybookResDTO.RecommendedStorybook toRecommendedStorybook(
            StorybookResDTO.RecommendedStorybook original, String reasonText) {
        return StorybookResDTO.RecommendedStorybook.builder()
                .storybookId(original.storybookId())
                .title(original.title())
                .shortDescription(original.shortDescription())
                .imageUrl(original.imageUrl())
                .recommendationReasonText(reasonText)
                .build();
    }

    public static StorybookResDTO.GetRecommendedStorybooks toRecommendedStorybooksResult(
            List<StorybookResDTO.RecommendedStorybook> recommendations) {
        return StorybookResDTO.GetRecommendedStorybooks.builder()
                .storybooks(recommendations)
                .build();
    }

    public static StorybookResDTO.InProgressStorybook toInProgressStorybook(
            Storybook storybook, Integer currentChapterOrder, boolean todayAvailable) {
        return StorybookResDTO.InProgressStorybook.builder()
                .storybookId(storybook.getId())
                .title(storybook.getTitle())
                .currentChapterOrder(currentChapterOrder)
                .totalChapterCount(10)
                .todayAvailable(todayAvailable)
                .build();
    }

    public static StorybookResDTO.BookshelfItem toBookshelfItem(Storybook sb, BookshelfStatus status) {
        return StorybookResDTO.BookshelfItem.builder()
                .storybookId(sb.getId())
                .title(sb.getTitle())
                .themeOrder(sb.getThemeOrder())
                .spineColor(sb.getSpineColor())
                .status(status)
                .build();
    }

    public static StorybookResDTO.GetHome toHome(
            HomeStatus homeStatus,
            String memberName,
            List<StorybookResDTO.InProgressStorybook> inProgressStorybooks,
            List<StorybookResDTO.BookshelfItem> bookshelf,
            List<StorybookResDTO.RecommendedStorybook> recommendedStorybooks) {
        return StorybookResDTO.GetHome.builder()
                .homeStatus(homeStatus)
                .memberName(memberName)
                .inProgressStorybooks(inProgressStorybooks)
                .bookshelf(bookshelf)
                .recommendedStorybooks(recommendedStorybooks)
                .build();
    }
}
