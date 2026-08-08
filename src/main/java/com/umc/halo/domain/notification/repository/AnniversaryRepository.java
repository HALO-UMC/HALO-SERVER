package com.umc.halo.domain.notification.repository;

import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.notification.entity.Anniversary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AnniversaryRepository extends JpaRepository<Anniversary, Long> {

    List<Anniversary> findAllByMemberOrderByAnniversaryDateAsc(Member member);

    void deleteByMemberId(Long memberId);

    List<Anniversary> findAllByIsRepeatedFalseAndIsLunarFalseAndAnniversaryDateBefore(LocalDate date);
    List<Anniversary> findAllByIsRepeatedFalseAndIsLunarTrue();

    @Query("""
        select a.member.id
        from Anniversary a
        where a.id = :anniversaryId
    """)
    Optional<Long> findMemberIdById(@Param("anniversaryId") Long anniversaryId);
}