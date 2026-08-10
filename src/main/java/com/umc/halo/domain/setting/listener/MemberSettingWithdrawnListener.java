package com.umc.halo.domain.setting.listener;

import com.umc.halo.domain.member.event.MemberWithdrawnEvent;
import com.umc.halo.domain.setting.repository.MemberSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberSettingWithdrawnListener {

    private final MemberSettingRepository memberSettingRepository;

    @Order(4)
    @EventListener
    public void handle(MemberWithdrawnEvent event) {
        Long memberId = event.memberId();

        memberSettingRepository.deleteByMemberId(memberId);
    }
}
