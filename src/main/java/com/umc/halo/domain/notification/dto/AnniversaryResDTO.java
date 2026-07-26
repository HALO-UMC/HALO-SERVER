package com.umc.halo.domain.notification.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public class AnniversaryResDTO {

    private AnniversaryResDTO() {
        throw new IllegalStateException("Utility class");
    }

    @Builder
    public record GetAnniversaries(
            List<Upcoming> upcomingAnniversaries,
            List<MyAnniversary> myAnniversaries,
            List<CommonAnniversaryInfo> commonAnniversaries
    ) {}

    @Builder
    public record Upcoming(
            Long anniversaryId,
            String title,
            LocalDate anniversaryDate,
            Integer dDay
    ) {}

    @Builder
    public record MyAnniversary(
            Long anniversaryId,
            String title,
            LocalDate anniversaryDate,
            Boolean isRepeated,
            Boolean sevenDaysAlarmEnabled,
            Boolean dayAlarmEnabled,
            String memo
    ) {}

    @Builder
    public record CommonAnniversaryInfo(
            Long commonAnniversaryId,
            String title,
            Integer month,
            Integer day,
            Boolean isLunar,
            Boolean sevenDaysAlarmEnabled,
            Boolean dayAlarmEnabled
    ) {}

    @Builder
    public record CreateAnniversary(
            Long anniversaryId
    ) {}

    @Builder
    public record UpdateAnniversary(
            Long anniversaryId
    ) {}
}
