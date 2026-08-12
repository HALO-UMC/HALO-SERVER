package com.umc.halo.domain.member.listener;

import com.umc.halo.domain.member.event.MemberWithdrawnEvent;
import com.umc.halo.domain.member.repository.MemberDeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberDeviceWithdrawnListener {

    private final MemberDeviceRepository memberDeviceRepository;

    @Order(8)
    @EventListener
    public void handle(MemberWithdrawnEvent event) {
        Long memberId = event.memberId();

        memberDeviceRepository.deleteByMemberId(memberId);
    }
}
