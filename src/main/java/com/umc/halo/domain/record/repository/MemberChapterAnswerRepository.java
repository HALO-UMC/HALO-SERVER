package com.umc.halo.domain.record.repository;

import com.umc.halo.domain.record.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface MemberChapterAnswerRepository extends JpaRepository<MemberChapterAnswer, Long> {
    void deleteByMemberChapterMemberId(Long memberId);
}
