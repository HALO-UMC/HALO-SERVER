package com.umc.halo.domain.term.repository;

import com.umc.halo.domain.term.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.*;
import com.umc.halo.domain.member.entity.Member;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberTermRepository extends JpaRepository<MemberTerm, Long> {
    List<MemberTerm> findAllByMember(Member member);
    Optional<MemberTerm> findByMemberAndTerm(Member member, Term term);
    void deleteByMemberId(Long memberId);
    @Query("""
    SELECT CASE WHEN COUNT(t) = (
        SELECT COUNT(mt)
        FROM MemberTerm mt
        WHERE mt.member.id = :memberId
        AND mt.isAgreed = true
        AND mt.term.isRequired = true
    )
    THEN true ELSE false END
    FROM Term t
    WHERE t.isRequired = true
    """)
    boolean areAllRequiredTermsAgreed(@Param("memberId") Long memberId);
}
