package com.umc.halo.domain.notification.listener;

import com.umc.halo.domain.member.event.MemberWithdrawnEvent;
import com.umc.halo.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationWithdrawnListener {

    private final NotificationRepository notificationRepository;

    @Order(5)
    @EventListener
    public void handle(MemberWithdrawnEvent event) {
        Long memberId = event.memberId();

        notificationRepository.deleteByMemberId(memberId);
    }
}
