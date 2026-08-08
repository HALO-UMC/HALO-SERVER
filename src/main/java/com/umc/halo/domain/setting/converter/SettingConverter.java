package com.umc.halo.domain.setting.converter;

import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.setting.dto.SettingResDTO;
import com.umc.halo.domain.setting.entity.Bgm;
import com.umc.halo.domain.setting.entity.MemberSetting;

import java.util.List;

public class SettingConverter {

    public static SettingResDTO.NotificationSettings toNotificationSettings(MemberSetting memberSetting) {
        return SettingResDTO.NotificationSettings.builder()
                .isAllNotificationEnabled(memberSetting.getIsAllNotificationEnabled())
                .regularNotificationTime(memberSetting.getRegularNotificationTime())
                .todayChapterNotificationEnabled(memberSetting.getTodayChapterNotificationEnabled())
                .retentionNotificationEnabled(memberSetting.getRetentionNotificationEnabled())
                .anniversaryNotificationEnabled(memberSetting.getAnniversaryNotificationEnabled())
                .build();
    }

    public static SettingResDTO.BgmSettings toBgmSettings(MemberSetting memberSetting) {
        return SettingResDTO.BgmSettings.builder()
                .bgmId(memberSetting.getBgm() != null ? memberSetting.getBgm().getId() : null)
                .bgmEnabled(memberSetting.getBgmEnabled())
                .bgmVolume(memberSetting.getBgmVolume())
                .build();
    }

    public static MemberSetting toMemberSetting(Member member, Bgm bgm) {
        return MemberSetting.builder()
                .member(member)
                .bgm(bgm)
                .build();
    }

    public static SettingResDTO.BgmInfo toBgmInfo(Bgm bgm) {
        return SettingResDTO.BgmInfo.builder()
                .bgmId(bgm.getId())
                .title(bgm.getTitle())
                .fileName(bgm.getFileName())
                .build();
    }

    public static SettingResDTO.Bgms toBgms(List<SettingResDTO.BgmInfo> bgms) {
        return SettingResDTO.Bgms.builder()
                .bgms(bgms)
                .build();
    }
}
