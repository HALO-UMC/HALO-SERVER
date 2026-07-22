package com.umc.halo.domain.setting.service;

import com.umc.halo.domain.setting.converter.SettingConverter;
import com.umc.halo.domain.setting.dto.SettingReqDTO;
import com.umc.halo.domain.setting.dto.SettingResDTO;
import com.umc.halo.domain.setting.entity.MemberSetting;
import com.umc.halo.domain.setting.exception.SettingException;
import com.umc.halo.domain.setting.exception.code.SettingErrorCode;
import com.umc.halo.domain.setting.repository.BgmRepository;
import com.umc.halo.domain.setting.repository.MemberSettingRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final MemberSettingRepository memberSettingRepository;
    private final BgmRepository bgmRepository;

    @Transactional(readOnly = true)
    public SettingResDTO.NotificationSettings getNotificationSettings(Long memberId) {
        MemberSetting memberSetting = memberSettingRepository.findByMemberId(memberId)
                .orElseThrow(() -> new SettingException(SettingErrorCode.SETTING_NOT_FOUND));

        return SettingConverter.toNotificationSettings(memberSetting);
    }

    @Transactional(readOnly = true)
    public SettingResDTO.Bgms getBgms() {
        List<SettingResDTO.BgmInfo> bgms = bgmRepository.findAll().stream().map(SettingConverter::toBgmInfo).toList();
        return SettingConverter.toBgms(bgms);
    }

    @Transactional
    public SettingResDTO.NotificationSettings updateNotificationSettings(Long memberId, SettingReqDTO.UpdateNotificationSettings dto) {
        MemberSetting memberSetting = memberSettingRepository.findByMemberId(memberId)
                .orElseThrow(() -> new SettingException(SettingErrorCode.SETTING_NOT_FOUND));

        if (dto.regularNotificationEnabled() && dto.regularNotificationTime() == null) {
            throw new SettingException(SettingErrorCode.INVALID_REGULAR_NOTIFICATION_TIME);
        }

        memberSetting.updateNotificationSettings(
                dto.regularNotificationEnabled(),
                dto.regularNotificationTime(),
                dto.todayChapterNotificationEnabled(),
                dto.retentionNotificationEnabled(),
                dto.anniversaryNotificationEnabled()
        );

        return SettingConverter.toNotificationSettings(memberSetting);
    }
}
