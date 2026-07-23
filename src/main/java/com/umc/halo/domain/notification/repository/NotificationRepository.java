package com.umc.halo.domain.notification.repository;

import com.umc.halo.domain.notification.entity.*;
import com.umc.halo.domain.notification.enums.NotificationType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    void deleteByMemberId(Long memberId);
    Optional<Notification> findByAnniversaryAndNotificationType(Anniversary anniversary, NotificationType notificationType);
    List<Notification> findAllByAnniversaryId(Long anniversaryId);
    void deleteAllByAnniversaryIdIn(List<Long> distinctIds);
}
