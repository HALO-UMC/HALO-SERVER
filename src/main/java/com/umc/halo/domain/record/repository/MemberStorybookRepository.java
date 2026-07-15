package com.umc.halo.domain.record.repository;

import com.umc.halo.domain.content.storybook.entity.*;
import com.umc.halo.domain.member.entity.*;
import com.umc.halo.domain.record.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface MemberStorybookRepository extends JpaRepository<MemberStorybook, Long> {
    Optional<MemberStorybook> findByStorybookAndMember(Storybook storybook, Member member);
    void deleteByMemberId(Long memberId);
}
