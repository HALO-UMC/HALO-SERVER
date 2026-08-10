package com.umc.halo.domain.term.listener;

import com.umc.halo.domain.member.event.MemberWithdrawnEvent;
import com.umc.halo.domain.term.repository.MemberTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MemberTermWithdrawnListener {

    private final MemberTermRepository memberTermRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handel(MemberWithdrawnEvent event) {
        Long memberId = event.memberId();

        memberTermRepository.deleteByMemberId(memberId);
    }
}
