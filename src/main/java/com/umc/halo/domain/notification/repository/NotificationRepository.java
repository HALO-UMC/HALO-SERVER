package com.umc.halo.domain.notification.repository;

import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.notification.entity.*;
import com.umc.halo.domain.notification.enums.NotificationStatus;
import com.umc.halo.domain.notification.enums.NotificationType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    void deleteByMemberId(Long memberId);
    Optional<Notification> findByAnniversaryIdAndNotificationTypeAndStatusIn(Long anniversaryId, NotificationType notificationType, List<NotificationStatus> statuses);
    void deleteAllByAnniversaryIdIn(List<Long> distinctIds);
    @Query("""
        select n
        from Notification n
        left join fetch n.anniversary
        where n.member.id = :memberId
        and n.notificationType in :notificationTypes
        and n.status in :statuses
    """)
    List<Notification> findAllWithAnniversary(@Param("memberId") Long memberId, @Param("notificationTypes") List<NotificationType> notificationTypes, @Param("statuses") List<NotificationStatus> statuses);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select n
        from Notification n
        where
        (
            n.status = :scheduled
            and n.scheduledAt <= :now
        )
        or
        (
            n.status = :processing
            and n.processingAt <= :timeout
        )
    """)
    List<Notification> findTargetsForUpdate( @Param("scheduled") NotificationStatus scheduled, @Param("processing") NotificationStatus processing, @Param("now") LocalDateTime now, @Param("timeout") LocalDateTime timeout);
    boolean existsByAnniversaryIdAndNotificationTypeAndScheduledAt(Long anniversaryId, NotificationType notificationType, LocalDateTime scheduledAt);
    @Query("""
        SELECT a
        FROM Anniversary a
        WHERE a.isRepeated = true
    """)
    List<Anniversary> findAllRepeatedAnniversaries();
    boolean existsByMemberAndNotificationTypeAndScheduledAt(Member member, NotificationType notificationType, LocalDateTime scheduledAt);

    List<Notification> findByMemberIdAndNotificationTypeAndStatusIn(Long memberId, NotificationType notificationType, List<NotificationStatus> scheduled);
}
