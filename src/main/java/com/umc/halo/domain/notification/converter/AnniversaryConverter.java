package com.umc.halo.domain.notification.converter;

import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.notification.dto.AnniversaryReqDTO;
import com.umc.halo.domain.notification.dto.AnniversaryResDTO;
import com.umc.halo.domain.notification.entity.Anniversary;
import com.umc.halo.domain.notification.entity.CommonAnniversary;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class AnniversaryConverter {

    private AnniversaryConverter() {
        throw new IllegalStateException("Utility class");
    }

    public static AnniversaryResDTO.GetAnniversaries toGetAnniversaries(
            List<AnniversaryResDTO.Upcoming> upcomingAnniversaries,
            List<AnniversaryResDTO.MyAnniversary> myAnniversaries,
            List<AnniversaryResDTO.CommonAnniversaryInfo> commonAnniversaries
    ) {
        return AnniversaryResDTO.GetAnniversaries.builder()
                .upcomingAnniversaries(upcomingAnniversaries)
                .myAnniversaries(myAnniversaries)
                .commonAnniversaries(commonAnniversaries)
                .build();
    }

    public static AnniversaryResDTO.Upcoming toUpcoming(Anniversary anniversary, LocalDate nextOccurrence, LocalDate today) {
        return AnniversaryResDTO.Upcoming.builder()
                .anniversaryId(anniversary.getId())
                .title(anniversary.getTitle())
                .anniversaryDate(nextOccurrence)
                .dDay((int) ChronoUnit.DAYS.between(today, nextOccurrence))
                .build();
    }

    public static AnniversaryResDTO.Upcoming toUpcomingFromCommon(CommonAnniversary commonAnniversary, LocalDate nextOccurrence, LocalDate today) {
        return AnniversaryResDTO.Upcoming.builder()
                .anniversaryId(null)
                .title(commonAnniversary.getTitle())
                .anniversaryDate(nextOccurrence)
                .dDay((int) ChronoUnit.DAYS.between(today, nextOccurrence))
                .build();
    }

    public static AnniversaryResDTO.MyAnniversary toMyAnniversary(Anniversary anniversary) {
        return AnniversaryResDTO.MyAnniversary.builder()
                .anniversaryId(anniversary.getId())
                .title(anniversary.getTitle())
                .anniversaryDate(anniversary.getAnniversaryDate())
                .isLunar(anniversary.getIsLunar())
                .isRepeated(anniversary.getIsRepeated())
                .sevenDaysAlarmEnabled(anniversary.getSevenDaysAlarmEnabled())
                .dayAlarmEnabled(anniversary.getDayAlarmEnabled())
                .memo(anniversary.getMemo())
                .build();
    }

    public static AnniversaryResDTO.CommonAnniversaryInfo toCommonAnniversaryInfo(CommonAnniversary commonAnniversary) {
        return AnniversaryResDTO.CommonAnniversaryInfo.builder()
                .commonAnniversaryId(commonAnniversary.getId())
                .title(commonAnniversary.getTitle())
                .month(commonAnniversary.getMonth())
                .day(commonAnniversary.getDay())
                .isLunar(commonAnniversary.getIsLunar())
                .sevenDaysAlarmEnabled(commonAnniversary.getSevenDaysAlarmEnabled())
                .dayAlarmEnabled(commonAnniversary.getDayAlarmEnabled())
                .build();
    }

    public static Anniversary toAnniversary(Member member, AnniversaryReqDTO.Create request) {
        return Anniversary.builder()
                .member(member)
                .title(request.title())
                .anniversaryDate(request.anniversaryDate())
                .isLunar(request.isLunar())
                .isRepeated(request.isRepeated())
                .sevenDaysAlarmEnabled(request.sevenDaysAlarmEnabled())
                .dayAlarmEnabled(request.dayAlarmEnabled())
                .memo(request.memo())
                .build();
    }

    public static AnniversaryResDTO.CreateAnniversary toCreateAnniversary(Anniversary anniversary) {
        return AnniversaryResDTO.CreateAnniversary.builder()
                .anniversaryId(anniversary.getId())
                .build();
    }

    public static AnniversaryResDTO.UpdateAnniversary toUpdateAnniversary(Anniversary anniversary) {
        return AnniversaryResDTO.UpdateAnniversary.builder()
                .anniversaryId(anniversary.getId())
                .build();
    }
}