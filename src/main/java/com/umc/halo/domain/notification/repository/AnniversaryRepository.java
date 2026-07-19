package com.umc.halo.domain.notification.repository;

import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.notification.entity.Anniversary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnniversaryRepository extends JpaRepository<Anniversary, Long> {

    List<Anniversary> findAllByMemberOrderByAnniversaryDateAsc(Member member);

    void deleteByMemberId(Long memberId);
}
