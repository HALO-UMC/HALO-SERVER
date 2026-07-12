package com.umc.halo.domain.record.repository;

import com.umc.halo.domain.record.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface MemberChapterAnswerRepository extends JpaRepository<MemberChapterAnswer, Long> {
    List<MemberChapterAnswer> findByMemberChapter(MemberChapter memberChapter);
}
