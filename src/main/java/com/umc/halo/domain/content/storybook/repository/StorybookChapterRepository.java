package com.umc.halo.domain.content.storybook.repository;

import com.umc.halo.domain.content.storybook.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface StorybookChapterRepository extends JpaRepository<StorybookChapter, Long> {

    Optional<StorybookChapter> findByStorybookIdAndChapterOrder(Long storybookId, Integer chapterOrder);
}
