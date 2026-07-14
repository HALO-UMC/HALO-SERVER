package com.umc.halo.domain.record.repository;

import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.record.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.List;

@Repository
public interface MemberChapterRepository extends JpaRepository<MemberChapter, Long> {

    List<MemberChapter> findByMemberAndStorybookChapter_Storybook_Id(Member member, Long storybookId);

    void deleteByMemberId(Long memberId);
}