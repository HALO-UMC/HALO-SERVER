package com.umc.halo.domain.record.repository;

import com.umc.halo.domain.content.storybook.entity.Storybook;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.record.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberStorybookRepository extends JpaRepository<MemberStorybook, Long> {

    boolean existsByMemberAndStorybook(Member member, Storybook storybook);

    Optional<MemberStorybook> findByMemberAndStorybook(Member member, Storybook storybook);

    List<MemberStorybook> findByMember(Member member);

    Optional<MemberStorybook> findByStorybookAndMember(Storybook storybook, Member member);
    Optional<MemberStorybook> findByMemberAndStorybook_Id(Member member, Long storybookId);
    void deleteByMemberId(Long memberId);

    @Query("select ms from MemberStorybook ms " +
            "join fetch ms.storybook " +
            "where ms.member = :member")
    List<MemberStorybook> findAllByMemberWithStorybook(@Param("member") Member member);
}
