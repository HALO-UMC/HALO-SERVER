package com.umc.halo.domain.notification.repository;

import com.umc.halo.domain.notification.entity.*;
import com.umc.halo.domain.notification.enums.NotificationStatus;
import com.umc.halo.domain.notification.enums.NotificationType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.*;

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
}
