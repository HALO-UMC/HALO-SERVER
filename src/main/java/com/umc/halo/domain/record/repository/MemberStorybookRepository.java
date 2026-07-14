package com.umc.halo.domain.record.repository;

import com.umc.halo.domain.content.storybook.entity.Storybook;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.record.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberStorybookRepository extends JpaRepository<MemberStorybook, Long> {

    boolean existsByMemberAndStorybook(Member member, Storybook storybook);

    Optional<MemberStorybook> findByMemberAndStorybook(Member member, Storybook storybook);

    List<MemberStorybook> findByMember(Member member);

    void deleteByMemberId(Long memberId);
}