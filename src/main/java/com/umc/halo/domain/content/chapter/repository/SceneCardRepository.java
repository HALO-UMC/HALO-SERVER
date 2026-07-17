package com.umc.halo.domain.content.chapter.repository;

import com.umc.halo.domain.content.chapter.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface SceneCardRepository extends JpaRepository<SceneCard, Long> {
    List<SceneCard> findByChapter(Chapter chapter);
}
