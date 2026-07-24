package com.umc.halo.domain.record.repository;

import com.umc.halo.domain.content.storybook.entity.*;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.record.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import com.umc.halo.domain.record.enums.Status;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MemberChapterRepository extends JpaRepository<MemberChapter, Long> {

    List<MemberChapter> findByMemberAndStorybookChapter_Storybook_Id(Member member, Long storybookId);

    MemberChapter findByMemberAndStorybookChapter(Member member, StorybookChapter storybookChapter);

    void deleteByMemberId(Long memberId);

    //월별 입니다!
    List<MemberChapter> findByMemberAndStatusAndCompletedDateBetween(
            Member member, Status status, LocalDate startDate, LocalDate endDate);
    //일별 입니다
    @Query("select mc from MemberChapter mc " +
            "join fetch mc.storybookChapter sc " +
            "join fetch sc.storybook " +
            "where mc.member = :member and mc.status = :status and mc.completedDate = :date")
    List<MemberChapter> findDailyWithStorybook(
            @Param("member") Member member,
            @Param("status") Status status,
            @Param("date") LocalDate date);

    @Query("select mc from MemberChapter mc " +
            "join fetch mc.storybookChapter sc " +
            "join fetch sc.storybook " +
            "where mc.member = :member")
    List<MemberChapter> findAllByMemberWithStorybookChapter(@Param("member") Member member);

    @Query("select mc from MemberChapter mc " +
            "join fetch mc.storybookChapter sc " +
            "left join fetch mc.sceneCard " +
            "where mc.member = :member and sc.storybook.id = :storybookId")
    List<MemberChapter> findAllByMemberAndStorybookIdWithSceneCard(
            @Param("member") Member member, @Param("storybookId") Long storybookId);
}
