package com.umc.halo.domain.notification.listener;

import com.umc.halo.domain.member.event.MemberWithdrawnEvent;
import com.umc.halo.domain.notification.repository.AnniversaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnniversaryWithdrawnListener {

    private final AnniversaryRepository anniversaryRepository;

    @Order(6)
    @EventListener
    public void handle(MemberWithdrawnEvent event) {
        Long memberId = event.memberId();

        anniversaryRepository.deleteByMemberId(memberId);
    }
}
