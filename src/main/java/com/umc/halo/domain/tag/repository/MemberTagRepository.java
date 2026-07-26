package com.umc.halo.domain.tag.repository;

import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.tag.entity.*;
import com.umc.halo.domain.tag.enums.Category;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.*;

import java.util.List;

@Repository
public interface MemberTagRepository extends JpaRepository<MemberTag, Long> {

    List<MemberTag> findByMemberAndTag_Category(Member member, Category category);

    @Query("SELECT mt FROM MemberTag mt JOIN FETCH mt.tag WHERE mt.member = :member")
    List<MemberTag> findAllByMember(@Param("member") Member member);

    void deleteByMemberId(Long memberId);

    void deleteByMemberAndTag_Category(Member member, Category category);
}
