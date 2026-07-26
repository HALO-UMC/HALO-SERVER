package com.umc.halo.domain.term.repository;

import com.umc.halo.domain.term.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import com.umc.halo.domain.member.entity.Member;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberTermRepository extends JpaRepository<MemberTerm, Long> {
    List<MemberTerm> findAllByMember(Member member);
    Optional<MemberTerm> findByMemberAndTerm(Member member, Term term);
    void deleteByMemberId(Long memberId);
}
