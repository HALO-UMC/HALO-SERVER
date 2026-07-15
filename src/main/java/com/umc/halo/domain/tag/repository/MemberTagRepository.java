package com.umc.halo.domain.tag.repository;

import com.umc.halo.domain.tag.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import com.umc.halo.domain.member.entity.Member;
import java.util.List;
import com.umc.halo.domain.tag.enums.Category;

@Repository
public interface MemberTagRepository extends JpaRepository<MemberTag, Long> {
    List<MemberTag> findAllByMember(Member member);
    void deleteByMemberId(Long memberId);
    void deleteByMemberAndTag_Category(Member member, Category category);
}
