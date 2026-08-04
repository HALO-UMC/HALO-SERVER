package com.umc.halo.domain.notification.service;

import com.umc.halo.domain.notification.entity.Notification;
import com.umc.halo.domain.notification.enums.NotificationStatus;
import com.umc.halo.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationTransactionService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public List<Notification> claimNotifications() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime timeOut = now.minusMinutes(10);

        List<Notification> notifications = notificationRepository.findTargetsForUpdate(NotificationStatus.SCHEDULED, NotificationStatus.PROCESSING, now, timeOut);

        notifications.forEach(Notification::startProcessing);

        return notifications;
    }

    @Transactional
    public void completeSend(Long notificationId){
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        notification.send();
    }


    @Transactional
    public void failSend(Long notificationId){
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        notification.fail();
    }

    @Transactional
    public void expireSend(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        notification.expire();
    }
}
