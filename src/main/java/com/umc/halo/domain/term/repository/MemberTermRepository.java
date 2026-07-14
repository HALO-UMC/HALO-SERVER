package com.umc.halo.domain.term.repository;

import com.umc.halo.domain.term.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface MemberTermRepository extends JpaRepository<MemberTerm, Long> {

    void deleteByMemberId(Long memberId);
}
