package com.umc.halo.domain.notification.service;

import com.github.usingsky.calendar.KoreanLunarCalendar;
import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.member.exception.MemberException;
import com.umc.halo.domain.member.exception.code.MemberErrorCode;
import com.umc.halo.domain.member.repository.MemberRepository;
import com.umc.halo.domain.notification.converter.AnniversaryConverter;
import com.umc.halo.domain.notification.dto.AnniversaryReqDTO;
import com.umc.halo.domain.notification.dto.AnniversaryResDTO;
import com.umc.halo.domain.notification.entity.Anniversary;
import com.umc.halo.domain.notification.entity.CommonAnniversary;
import com.umc.halo.domain.notification.exception.AnniversaryErrorCode;
import com.umc.halo.domain.notification.exception.AnniversaryException;
import com.umc.halo.domain.notification.repository.AnniversaryRepository;
import com.umc.halo.domain.notification.repository.CommonAnniversaryRepository;
import com.umc.halo.global.ai.event.AnniversaryCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AnniversaryService {

    private final AnniversaryRepository anniversaryRepository;
    private final CommonAnniversaryRepository commonAnniversaryRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional(readOnly = true)
    public AnniversaryResDTO.GetAnniversaries getAnniversaries(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        LocalDate today = LocalDate.now();

        List<Anniversary> myAnniversaryEntities = anniversaryRepository.findAllByMemberOrderByAnniversaryDateAsc(member);
        List<CommonAnniversary> commonAnniversaryEntities = commonAnniversaryRepository.findAll();

        List<AnniversaryResDTO.Upcoming> upcomingAnniversaries = buildUpcomingList(
                myAnniversaryEntities, commonAnniversaryEntities, today);

        List<AnniversaryResDTO.MyAnniversary> myAnniversaries = myAnniversaryEntities.stream()
                .map(AnniversaryConverter::toMyAnniversary)
                .toList();

        List<AnniversaryResDTO.CommonAnniversaryInfo> commonAnniversaries = commonAnniversaryEntities.stream()
                .map(AnniversaryConverter::toCommonAnniversaryInfo)
                .toList();

        return AnniversaryConverter.toGetAnniversaries(upcomingAnniversaries, myAnniversaries, commonAnniversaries);
    }

    private List<AnniversaryResDTO.Upcoming> buildUpcomingList(
            List<Anniversary> myAnniversaryEntities,
            List<CommonAnniversary> commonAnniversaryEntities,
            LocalDate today
    ) {
        List<AnniversaryResDTO.Upcoming> upcomingFromMyAnniversaries = myAnniversaryEntities.stream()
                .map(anniversary -> {
                    LocalDate nextOccurrence = resolveNextOccurrence(anniversary, today);
                    if (nextOccurrence == null) {
                        return null;
                    }
                    return AnniversaryConverter.toUpcoming(anniversary, nextOccurrence, today);
                })
                .filter(Objects::nonNull)
                .toList();

        List<AnniversaryResDTO.Upcoming> upcomingFromCommonAnniversaries = commonAnniversaryEntities.stream()
                .map(commonAnniversary -> {
                    LocalDate nextOccurrence = resolveNextOccurrence(commonAnniversary, today);
                    if (nextOccurrence == null) {
                        return null;
                    }
                    return AnniversaryConverter.toUpcomingFromCommon(commonAnniversary, nextOccurrence, today);
                })
                .filter(Objects::nonNull)
                .toList();

        return java.util.stream.Stream.concat(upcomingFromMyAnniversaries.stream(), upcomingFromCommonAnniversaries.stream())
                .sorted(Comparator.comparing(AnniversaryResDTO.Upcoming::dDay))
                .toList();
    }

    private LocalDate resolveNextOccurrence(Anniversary anniversary, LocalDate today) {
        if (!Boolean.TRUE.equals(anniversary.getIsRepeated())) {
            LocalDate date = anniversary.getAnniversaryDate();
            return date.isBefore(today) ? null : date;
        }
        LocalDate thisYear = anniversary.getAnniversaryDate().withYear(today.getYear());
        return thisYear.isBefore(today) ? anniversary.getAnniversaryDate().withYear(today.getYear() + 1) : thisYear;
    }

    private LocalDate resolveNextOccurrence(CommonAnniversary commonAnniversary, LocalDate today) {
        if (Boolean.TRUE.equals(commonAnniversary.getIsLunar())) {
            return resolveNextLunarOccurrence(commonAnniversary, today);
        }
        MonthDay monthDay = MonthDay.of(commonAnniversary.getMonth(), commonAnniversary.getDay());
        LocalDate thisYear = monthDay.atYear(today.getYear());
        return thisYear.isBefore(today) ? monthDay.atYear(today.getYear() + 1) : thisYear;
    }

    private LocalDate resolveNextLunarOccurrence(CommonAnniversary commonAnniversary, LocalDate today) {
        LocalDate thisYearSolar = convertLunarToSolar(today.getYear(), commonAnniversary.getMonth(), commonAnniversary.getDay());
        if (thisYearSolar != null && !thisYearSolar.isBefore(today)) {
            return thisYearSolar;
        }
        return convertLunarToSolar(today.getYear() + 1, commonAnniversary.getMonth(), commonAnniversary.getDay());
    }

    // 음력 날짜(윤달 아님)를 해당 연도의 양력 날짜로 변환. 지원 범위를 벗어나거나 변환 실패 시 null 반환
    private LocalDate convertLunarToSolar(int lunarYear, int lunarMonth, int lunarDay) {
        KoreanLunarCalendar calendar = KoreanLunarCalendar.getInstance();
        boolean success = calendar.setLunarDate(lunarYear, lunarMonth, lunarDay, false);
        if (!success) {
            return null;
        }
        return LocalDate.of(calendar.getSolarYear(), calendar.getSolarMonth(), calendar.getSolarDay());
    }

    @Transactional
    public AnniversaryResDTO.CreateAnniversary createAnniversary(Long memberId, AnniversaryReqDTO.Create request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        Anniversary anniversary = AnniversaryConverter.toAnniversary(member, request);
        Anniversary savedAnniversary = anniversaryRepository.save(anniversary);

        applicationEventPublisher.publishEvent(new AnniversaryCreatedEvent(savedAnniversary.getId()));

        return AnniversaryConverter.toCreateAnniversary(savedAnniversary);
    }

    @Transactional
    public AnniversaryResDTO.UpdateAnniversary updateAnniversary(Long memberId, Long anniversaryId, AnniversaryReqDTO.Update request) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        Anniversary anniversary = getOwnedAnniversary(memberId, anniversaryId);
        anniversary.update(
                request.title(),
                request.anniversaryDate(),
                request.sevenDaysAlarmEnabled(),
                request.dayAlarmEnabled(),
                request.memo()
        );
        return AnniversaryConverter.toUpdateAnniversary(anniversary);
    }

    @Transactional
    public void deleteAnniversaries(Long memberId, List<Long> anniversaryIds) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        List<Long> distinctIds = anniversaryIds.stream().distinct().toList();
        List<Anniversary> anniversaries = anniversaryRepository.findAllById(distinctIds);

        if (anniversaries.size() != distinctIds.size()) {
            throw new AnniversaryException(AnniversaryErrorCode.ANNIVERSARY_NOT_FOUND);
        }

        boolean hasUnauthorized = anniversaries.stream()
                .anyMatch(anniversary -> !anniversary.getMember().getId().equals(memberId));
        if (hasUnauthorized) {
            throw new AnniversaryException(AnniversaryErrorCode.ANNIVERSARY_ACCESS_DENIED);
        }

        anniversaryRepository.deleteAll(anniversaries);
    }

    private Anniversary getOwnedAnniversary(Long memberId, Long anniversaryId) {
        Anniversary anniversary = anniversaryRepository.findById(anniversaryId)
                .orElseThrow(() -> new AnniversaryException(AnniversaryErrorCode.ANNIVERSARY_NOT_FOUND));
        if (!anniversary.getMember().getId().equals(memberId)) {
            throw new AnniversaryException(AnniversaryErrorCode.ANNIVERSARY_ACCESS_DENIED);
        }
        return anniversary;
    }
}