package com.umc.halo.domain.record.repository;

import com.umc.halo.domain.content.chapter.entity.*;
import com.umc.halo.domain.member.entity.*;
import com.umc.halo.domain.record.entity.*;
import com.umc.halo.domain.record.enums.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;

import java.time.*;
import java.util.*;

@Repository
public interface MemberChapterRepository extends JpaRepository<MemberChapter, Long> {

    List<MemberChapter> findByMemberAndChapter_Storybook_Id(Member member, Long storybookId);

    MemberChapter findByMemberAndChapter(Member member, Chapter chapter);

    void deleteByMemberId(Long memberId);

    //월별 입니다!
    List<MemberChapter> findByMemberAndStatusAndCompletedDateBetween(
            Member member, Status status, LocalDate startDate, LocalDate endDate);

    //일별 입니다
    @Query("select mc from MemberChapter mc " +
            "join fetch mc.chapter chapter " +
            "join fetch chapter.storybook " +
            "where mc.member = :member and mc.status = :status and mc.completedDate = :date")
    List<MemberChapter> findDailyWithStorybook(
            @Param("member") Member member,
            @Param("status") Status status,
            @Param("date") LocalDate date);

    @Query("select mc from MemberChapter mc " +
            "join fetch mc.chapter chapter " +
            "join fetch chapter.storybook " +
            "where mc.member = :member")
    List<MemberChapter> findAllByMemberWithChapter(@Param("member") Member member);

    @Query("select mc from MemberChapter mc " +
            "join fetch mc.chapter chapter " +
            "left join fetch mc.sceneCard " +
            "where mc.member = :member and chapter.storybook.id = :storybookId")
    List<MemberChapter> findAllByMemberAndStorybookIdWithSceneCard(
            @Param("member") Member member, @Param("storybookId") Long storybookId);
}
