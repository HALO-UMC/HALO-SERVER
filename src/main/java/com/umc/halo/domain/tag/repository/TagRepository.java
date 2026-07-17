package com.umc.halo.domain.tag.repository;

import com.umc.halo.domain.tag.entity.*;
import com.umc.halo.domain.tag.enums.Category;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByCategory(Category category);
}