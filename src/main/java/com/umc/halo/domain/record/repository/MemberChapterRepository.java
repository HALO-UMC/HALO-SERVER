package com.umc.halo.domain.record.repository;

import com.umc.halo.domain.record.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface MemberChapterRepository extends JpaRepository<MemberChapter, Long> {
    void deleteByMemberId(Long memberId);
}
