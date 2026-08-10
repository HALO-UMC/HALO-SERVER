package com.umc.halo.domain.tag.listener;

import com.umc.halo.domain.member.event.MemberWithdrawnEvent;
import com.umc.halo.domain.tag.repository.MemberTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MemberTagWithdrawnListener {

    private final MemberTagRepository memberTagRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(MemberWithdrawnEvent event) {
        Long memberId = event.memberId();

        memberTagRepository.deleteByMemberId(memberId);
    }
}
