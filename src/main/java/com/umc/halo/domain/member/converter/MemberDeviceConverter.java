package com.umc.halo.domain.member.converter;

import com.umc.halo.domain.member.dto.MemberDeviceReqDTO;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.entity.MemberDevice;

public class MemberDeviceConverter {

    public static MemberDevice toMemberDevice(Member member, MemberDeviceReqDTO.Register dto) {
        return MemberDevice.builder()
                .member(member)
                .fcmToken(dto.fcmToken())
                .deviceType(dto.deviceType())
                .deviceIdentifier(dto.deviceIdentifier())
                .build();
    }
}
