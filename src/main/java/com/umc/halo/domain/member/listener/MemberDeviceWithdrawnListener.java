package com.umc.halo.domain.member.listener;

import com.umc.halo.domain.member.event.MemberWithdrawnEvent;
import com.umc.halo.domain.member.repository.MemberDeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MemberDeviceWithdrawnListener {

    private final MemberDeviceRepository memberDeviceRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(MemberWithdrawnEvent event) {
        Long memberId = event.memberId();

        memberDeviceRepository.deleteByMemberId(memberId);
    }
}
