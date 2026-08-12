package com.umc.halo.domain.tag.listener;

import com.umc.halo.domain.member.event.MemberWithdrawnEvent;
import com.umc.halo.domain.tag.repository.MemberTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberTagWithdrawnListener {

    private final MemberTagRepository memberTagRepository;

    @Order(7)
    @EventListener
    public void handle(MemberWithdrawnEvent event) {
        Long memberId = event.memberId();

        memberTagRepository.deleteByMemberId(memberId);
    }
}
