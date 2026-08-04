package com.umc.halo.domain.member.service;

import com.umc.halo.domain.member.converter.MemberDeviceConverter;
import com.umc.halo.domain.member.dto.MemberDeviceReqDTO;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.entity.MemberDevice;
import com.umc.halo.domain.member.exception.MemberException;
import com.umc.halo.domain.member.exception.code.MemberDeviceErrorCode;
import com.umc.halo.domain.member.exception.code.MemberErrorCode;
import com.umc.halo.domain.member.repository.MemberDeviceRepository;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.notification.entity.Notification;
import com.umc.halo.domain.notification.enums.NotificationStatus;
import com.umc.halo.domain.notification.enums.NotificationType;
import com.umc.halo.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MemberDeviceService {

    private final MemberRepository memberRepository;
    private final MemberDeviceRepository memberDeviceRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public void registerDevice(Long memberId, MemberDeviceReqDTO.Register dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        MemberDevice memberDevice = memberDeviceRepository.findByMemberAndDeviceIdentifier(member, dto.deviceIdentifier()).orElse(null);

        if (memberDevice != null) {
            memberDevice.update(dto.fcmToken(), dto.deviceType());
            return;
        }

        memberDeviceRepository.save(MemberDeviceConverter.toMemberDevice(member, dto));
    }

    @Transactional
    public void deleteDevice(Long memberId, String deviceIdentifier) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        MemberDevice memberDevice = memberDeviceRepository.findByMemberAndDeviceIdentifier(member, deviceIdentifier)
                .orElseThrow(() -> new MemberException(MemberDeviceErrorCode.DEVICE_NOT_FOUND));

        memberDeviceRepository.delete(memberDevice);
    }
}
