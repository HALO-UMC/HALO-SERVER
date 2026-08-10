package com.umc.halo.domain.member.listener;

import com.umc.halo.domain.member.event.MemberWithdrawnEvent;
import com.umc.halo.domain.member.repository.MemberDeviceRepository;
import com.umc.halo.domain.notification.repository.AnniversaryRepository;
import com.umc.halo.domain.notification.repository.NotificationRepository;
import com.umc.halo.domain.record.repository.MemberChapterAnswerRepository;
import com.umc.halo.domain.record.repository.MemberChapterRepository;
import com.umc.halo.domain.record.repository.MemberStorybookRepository;
import com.umc.halo.domain.setting.repository.MemberSettingRepository;
import com.umc.halo.domain.tag.repository.MemberTagRepository;
import com.umc.halo.domain.term.repository.MemberTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MemberWithdrawnListener {

    private final MemberChapterAnswerRepository memberChapterAnswerRepository;
    private final MemberChapterRepository memberChapterRepository;
    private final MemberStorybookRepository memberStorybookRepository;
    private final MemberTermRepository memberTermRepository;
    private final MemberSettingRepository memberSettingRepository;
    private final NotificationRepository notificationRepository;
    private final AnniversaryRepository anniversaryRepository;
    private final MemberTagRepository memberTagRepository;
    private final MemberDeviceRepository memberDeviceRepository;

    @Transactional
    @EventListener
    public void handleMemberWithdrawn(MemberWithdrawnEvent event) {
        Long memberId = event.memberId();

        memberChapterAnswerRepository.deleteByMemberChapterMemberId(memberId);
        memberChapterRepository.deleteByMemberId(memberId);
        memberStorybookRepository.deleteByMemberId(memberId);
        memberTermRepository.deleteByMemberId(memberId);
        memberSettingRepository.deleteByMemberId(memberId);
        notificationRepository.deleteByMemberId(memberId);
        anniversaryRepository.deleteByMemberId(memberId);
        memberTagRepository.deleteByMemberId(memberId);
        memberDeviceRepository.deleteByMemberId(memberId);
    }
}
