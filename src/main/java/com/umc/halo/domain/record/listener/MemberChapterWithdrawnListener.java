package com.umc.halo.domain.record.listener;

import com.umc.halo.domain.member.event.MemberWithdrawnEvent;
import com.umc.halo.domain.record.repository.MemberChapterAnswerRepository;
import com.umc.halo.domain.record.repository.MemberChapterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MemberChapterWithdrawnListener {

    private final MemberChapterAnswerRepository memberChapterAnswerRepository;
    private final MemberChapterRepository memberChapterRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(MemberWithdrawnEvent event) {
        Long memberId = event.memberId();

        memberChapterAnswerRepository.deleteByMemberChapterMemberId(memberId);
        memberChapterRepository.deleteByMemberId(memberId);
    }
}
