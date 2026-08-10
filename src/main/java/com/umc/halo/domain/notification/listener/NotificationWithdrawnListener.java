package com.umc.halo.domain.notification.listener;

import com.umc.halo.domain.member.event.MemberWithdrawnEvent;
import com.umc.halo.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationWithdrawnListener {

    private final NotificationRepository notificationRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(MemberWithdrawnEvent event) {
        Long memberId = event.memberId();

        notificationRepository.deleteByMemberId(memberId);
    }
}
