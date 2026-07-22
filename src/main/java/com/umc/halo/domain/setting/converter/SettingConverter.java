package com.umc.halo.domain.setting.converter;

import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.setting.dto.SettingResDTO;
import com.umc.halo.domain.setting.entity.Bgm;
import com.umc.halo.domain.setting.entity.MemberSetting;

import java.util.List;

public class SettingConverter {

    public static SettingResDTO.NotificationSettings toNotificationSettings(MemberSetting memberSetting) {
        boolean isAllNotificationEnabled =
                memberSetting.getRegularNotificationEnabled()
                && memberSetting.getTodayChapterNotificationEnabled()
                && memberSetting.getRetentionNotificationEnabled()
                && memberSetting.getAnniversaryNotificationEnabled();

        return SettingResDTO.NotificationSettings.builder()
                .regularNotificationEnabled(memberSetting.getRegularNotificationEnabled())
                .regularNotificationTime(memberSetting.getRegularNotificationTime())
                .todayChapterNotificationEnabled(memberSetting.getTodayChapterNotificationEnabled())
                .retentionNotificationEnabled(memberSetting.getRetentionNotificationEnabled())
                .anniversaryNotificationEnabled(memberSetting.getAnniversaryNotificationEnabled())
                .isAllNotificationEnabled(isAllNotificationEnabled)
                .build();
    }

    public static MemberSetting toMemberSetting(Member member) {
        return MemberSetting.builder()
                .member(member)
                .build();
    }

    public static SettingResDTO.BgmInfo toBgmInfo(Bgm bgm) {
        return SettingResDTO.BgmInfo.builder()
                .bgmId(bgm.getId())
                .title(bgm.getTitle())
                .fileName(bgm.getFileName())
                .imageName(bgm.getImageName())
                .build();
    }

    public static SettingResDTO.Bgms toBgms(List<SettingResDTO.BgmInfo> bgms) {
        return SettingResDTO.Bgms.builder()
                .bgms(bgms)
                .build();
    }
}
