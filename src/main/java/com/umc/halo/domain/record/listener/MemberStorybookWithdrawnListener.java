package com.umc.halo.domain.record.listener;

import com.umc.halo.domain.member.event.MemberWithdrawnEvent;
import com.umc.halo.domain.record.repository.MemberStorybookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MemberStorybookWithdrawnListener {

    private final MemberStorybookRepository memberStorybookRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(MemberWithdrawnEvent event) {
        Long memberId = event.memberId();

        memberStorybookRepository.deleteByMemberId(memberId);
    }
}
