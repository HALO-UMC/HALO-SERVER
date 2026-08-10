package com.umc.halo.domain.record.listener;

import com.umc.halo.domain.member.event.MemberWithdrawnEvent;
import com.umc.halo.domain.record.repository.MemberChapterAnswerRepository;
import com.umc.halo.domain.record.repository.MemberChapterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberChapterWithdrawnListener {

    private final MemberChapterAnswerRepository memberChapterAnswerRepository;
    private final MemberChapterRepository memberChapterRepository;

    @Order(1)
    @EventListener
    public void handle(MemberWithdrawnEvent event) {
        Long memberId = event.memberId();

        memberChapterAnswerRepository.deleteByMemberChapterMemberId(memberId);
        memberChapterRepository.deleteByMemberId(memberId);
    }
}
