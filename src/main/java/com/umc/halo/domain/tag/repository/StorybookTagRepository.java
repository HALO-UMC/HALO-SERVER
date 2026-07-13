package com.umc.halo.domain.tag.repository;

import com.umc.halo.domain.tag.entity.*;
import com.umc.halo.domain.tag.enums.PriorityLevel;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.List;

@Repository
public interface StorybookTagRepository extends JpaRepository<StorybookTag, Long> {

    List<StorybookTag> findByTagAndPriorityLevel(Tag tag, PriorityLevel priorityLevel);
}