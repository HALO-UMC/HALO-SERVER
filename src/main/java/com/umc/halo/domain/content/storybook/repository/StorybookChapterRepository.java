package com.umc.halo.domain.content.storybook.repository;

import com.umc.halo.domain.content.storybook.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface StorybookChapterRepository extends JpaRepository<StorybookChapter, Long> {

    List<StorybookChapter> findByStorybook_IdOrderByChapterOrderAsc(Long storybookId);

    Optional<StorybookChapter> findByStorybook_IdAndChapterOrder(Long storybookId, Integer chapterOrder);

    Optional<StorybookChapter> findByStorybookIdAndChapterOrder(Long storybookId, Integer chapterOrder);

    List<StorybookChapter> findByStorybook_IdIn(List<Long> storybookIds);
}
