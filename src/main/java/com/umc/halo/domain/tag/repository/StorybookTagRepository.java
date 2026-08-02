package com.umc.halo.domain.tag.repository;

import com.umc.halo.domain.content.storybook.entity.Storybook;
import com.umc.halo.domain.tag.entity.*;
import com.umc.halo.domain.tag.enums.PriorityLevel;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface StorybookTagRepository extends JpaRepository<StorybookTag, Long> {

    List<StorybookTag> findByTagAndPriorityLevel(Tag tag, PriorityLevel priorityLevel);

    List<StorybookTag> findByTagIn(List<Tag> tags);

    List<StorybookTag> findByTagInAndPriorityLevel(List<Tag> tags, PriorityLevel priorityLevel);

    Optional<StorybookTag> findByStorybookAndPriorityLevel(Storybook storybook, PriorityLevel priorityLevel);
}