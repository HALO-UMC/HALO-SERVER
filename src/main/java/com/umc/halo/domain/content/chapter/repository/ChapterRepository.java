package com.umc.halo.domain.content.chapter.repository;

import com.umc.halo.domain.content.chapter.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findByStorybook_IdOrderByChapterOrderAsc(Long storybookId);

    Optional<Chapter> findByStorybook_IdAndChapterOrder(Long storybookId, Integer chapterOrder);

    List<Chapter> findByStorybook_IdIn(List<Long> storybookIds);
}
