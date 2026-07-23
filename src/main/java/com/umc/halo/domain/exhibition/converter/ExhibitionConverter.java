package com.umc.halo.domain.exhibition.converter;

import com.umc.halo.domain.content.storybook.dto.StorybookResDTO;
import com.umc.halo.domain.content.storybook.entity.Storybook;
import com.umc.halo.domain.content.storybook.entity.StorybookChapter;
import com.umc.halo.domain.content.storybook.entity.StorybookCharacter;
import com.umc.halo.domain.exhibition.dto.ExhibitionChapterResDTO;
import com.umc.halo.domain.exhibition.dto.ExhibitionResDTO;
import com.umc.halo.domain.record.entity.MemberChapter;
import com.umc.halo.domain.record.entity.MemberStorybook;

import java.util.List;

public class ExhibitionConverter {


    public static ExhibitionResDTO.MainInfo toMainInfo(
            int collectedCharacterCount,
            int inProgressStorybookCount,
            Long currentStorybookId,
            List<ExhibitionResDTO.CompletedStorybook> storybooks,
            List<ExhibitionResDTO.InProgressStorybook> inProgressStorybooks,
            List<ExhibitionResDTO.RecommendedStorybook> recommendedStorybooks) {

        return new ExhibitionResDTO.MainInfo(
                new ExhibitionResDTO.Stats(collectedCharacterCount, inProgressStorybookCount),
                currentStorybookId,
                storybooks,
                inProgressStorybooks,
                recommendedStorybooks
        );
    }
    public static ExhibitionResDTO.CompletedStorybook toCompletedStorybook(
            MemberStorybook ms, StorybookCharacter character) {
        Storybook sb = ms.getStorybook();
        return new ExhibitionResDTO.CompletedStorybook(
                sb.getId(),
                sb.getThemeOrder(),
                sb.getTitle(),
                sb.getShortDescription(),
                ms.getLastCompletedDate(),
                character.getId(),
                character.getName(),
                character.getImageUrl()
        );
    }

    public static ExhibitionResDTO.InProgressStorybook toInProgressStorybook(MemberStorybook ms) {
        Storybook sb = ms.getStorybook();
        return new ExhibitionResDTO.InProgressStorybook(
                sb.getId(),
                sb.getTitle(),
                ms.getLastChapterOrder()
        );
    }

    public static ExhibitionResDTO.RecommendedStorybook toRecommendedStorybook(
            StorybookResDTO.RecommendedStorybook src) {
        return new ExhibitionResDTO.RecommendedStorybook(
                src.storybookId(),
                src.title(),
                src.shortDescription(),
                src.imageUrl(),
                src.recommendationReasonText()
        );
    }


    public static ExhibitionChapterResDTO.ChaptersInfo toChaptersInfo(
            Long storybookId, List<ExhibitionChapterResDTO.ChapterInfo> chapters) {
        return new ExhibitionChapterResDTO.ChaptersInfo(storybookId, chapters);
    }

    public static ExhibitionChapterResDTO.ChapterInfo toChapterInfo(
            StorybookChapter sc, MemberChapter mc, String chapterImageUrl) {
        return new ExhibitionChapterResDTO.ChapterInfo(
                sc.getChapterOrder(),
                chapterImageUrl,
                sc.getChapter().getTitle(),
                mc.getSummary(),
                mc.getCompletedDate(),
                mc.getEmotion()
        );
    }
}