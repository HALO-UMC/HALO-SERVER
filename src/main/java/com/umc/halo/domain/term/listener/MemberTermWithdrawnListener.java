package com.umc.halo.domain.term.listener;

import com.umc.halo.domain.member.event.MemberWithdrawnEvent;
import com.umc.halo.domain.term.repository.MemberTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberTermWithdrawnListener {

    private final MemberTermRepository memberTermRepository;

    @Order(3)
    @EventListener
    public void handle(MemberWithdrawnEvent event) {
        Long memberId = event.memberId();

        memberTermRepository.deleteByMemberId(memberId);
    }
}
