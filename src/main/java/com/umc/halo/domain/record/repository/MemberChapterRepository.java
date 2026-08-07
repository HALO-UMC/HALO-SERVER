package com.umc.halo.domain.record.repository;

import com.umc.halo.domain.content.chapter.entity.*;
import com.umc.halo.domain.member.entity.*;
import com.umc.halo.domain.record.entity.*;
import com.umc.halo.domain.record.enums.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    List<MemberChapter> findByMember(Member member);

    List<MemberChapter> findAllByMemberIn(List<Member> members);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select mc from MemberChapter mc where mc.id = :id")
    Optional<MemberChapter> findByIdForUpdate(@Param("id") Long id);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query("update MemberChapter mc set mc.imageKey = :finalKey where mc.id = :id and mc.imageKey = :pendingKey")
    int updateImageKeyIfPending(@Param("id") Long id, @Param("pendingKey") String pendingKey, @Param("finalKey") String finalKey);
}
