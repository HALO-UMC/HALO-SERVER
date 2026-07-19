package com.umc.halo.domain.setting.service;

import com.umc.halo.domain.member.exception.MemberException;
import com.umc.halo.domain.member.exception.code.MemberErrorCode;
import com.umc.halo.domain.setting.converter.SettingConverter;
import com.umc.halo.domain.setting.dto.SettingResDTO;
import com.umc.halo.domain.setting.entity.MemberSetting;
import com.umc.halo.domain.setting.exception.SettingException;
import com.umc.halo.domain.setting.exception.code.SettingErrorCode;
import com.umc.halo.domain.setting.repository.BgmRepository;
import com.umc.halo.domain.setting.repository.MemberSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final MemberSettingRepository memberSettingRepository;
    private final BgmRepository bgmRepository;

    @Transactional(readOnly = true)
    public SettingResDTO.NotificationSettings getNotificationSettings(Long memberId) {
        MemberSetting memberSetting = memberSettingRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        return SettingConverter.toNotificationSettings(memberSetting);
    }
}
