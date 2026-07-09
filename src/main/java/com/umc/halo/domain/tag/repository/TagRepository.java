package com.umc.halo.domain.tag.repository;

import com.umc.halo.domain.tag.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
}
