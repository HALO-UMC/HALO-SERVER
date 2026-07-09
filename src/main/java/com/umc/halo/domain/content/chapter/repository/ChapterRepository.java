package com.umc.halo.domain.content.chapter.repository;

import com.umc.halo.domain.content.chapter.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
}
