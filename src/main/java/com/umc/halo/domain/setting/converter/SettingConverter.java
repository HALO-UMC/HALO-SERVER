package com.umc.halo.domain.setting.converter;

import com.umc.halo.domain.setting.dto.SettingResDTO;
import com.umc.halo.domain.setting.entity.MemberSetting;

public class SettingConverter {

    public static SettingResDTO.NotificationSetting toNotificationSetting(MemberSetting memberSetting
    ) {
        boolean isAllNotificationEnabled =
                memberSetting.getRegularNotificationEnabled()
                && memberSetting.getTodayChapterNotificationEnabled()
                && memberSetting.getRetentionNotificationEnabled()
                && memberSetting.getAnniversaryNotificationEnabled();

        return SettingResDTO.NotificationSetting.builder()
                .regularNotificationEnabled(memberSetting.getRegularNotificationEnabled())
                .regularNotificationTime(memberSetting.getRegularNotificationTime())
                .todayChapterNotificationEnabled(memberSetting.getTodayChapterNotificationEnabled())
                .retentionNotificationEnabled(memberSetting.getRetentionNotificationEnabled())
                .anniversaryNotificationEnabled(memberSetting.getAnniversaryNotificationEnabled())
                .isAllNotificationEnabled(isAllNotificationEnabled)
                .build();
    }
}
