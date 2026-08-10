package com.umc.halo.domain.record.listener;

import com.umc.halo.domain.member.event.MemberWithdrawnEvent;
import com.umc.halo.domain.record.repository.MemberStorybookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberStorybookWithdrawnListener {

    private final MemberStorybookRepository memberStorybookRepository;

    @Order(2)
    @EventListener
    public void handle(MemberWithdrawnEvent event) {
        Long memberId = event.memberId();

        memberStorybookRepository.deleteByMemberId(memberId);
    }
}
