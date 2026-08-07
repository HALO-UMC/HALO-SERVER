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
import com.umc.halo.domain.notification.exception.code.AnniversaryErrorCode;
import com.umc.halo.domain.notification.exception.AnniversaryException;
import com.umc.halo.domain.notification.repository.AnniversaryRepository;
import com.umc.halo.domain.notification.repository.CommonAnniversaryRepository;
import com.umc.halo.domain.notification.repository.NotificationRepository;
import com.umc.halo.global.ai.event.AnniversaryCreatedEvent;
import com.umc.halo.global.ai.event.AnniversaryUpdatedEvent;
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
    private final NotificationRepository notificationRepository;
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
        if (Boolean.TRUE.equals(anniversary.getIsLunar())) {
            return resolveNextOccurrenceLunar(anniversary, today);
        }
        if (!Boolean.TRUE.equals(anniversary.getIsRepeated())) {
            LocalDate date = anniversary.getAnniversaryDate();
            return date.isBefore(today) ? null : date;
        }
        LocalDate thisYear = anniversary.getAnniversaryDate().withYear(today.getYear());
        return thisYear.isBefore(today) ? anniversary.getAnniversaryDate().withYear(today.getYear() + 1) : thisYear;
    }

    private LocalDate resolveNextOccurrenceLunar(Anniversary anniversary, LocalDate today) {
        int lunarMonth = anniversary.getAnniversaryDate().getMonthValue();
        int lunarDay = anniversary.getAnniversaryDate().getDayOfMonth();

        if (!Boolean.TRUE.equals(anniversary.getIsRepeated())) {
            LocalDate solarDate = convertLunarToSolar(anniversary.getAnniversaryDate().getYear(), lunarMonth, lunarDay);
            return (solarDate != null && !solarDate.isBefore(today)) ? solarDate : null;
        }

        return java.util.stream.Stream.of(today.getYear() - 1, today.getYear(), today.getYear() + 1)
                .map(year -> convertLunarToSolar(year, lunarMonth, lunarDay))
                .filter(java.util.Objects::nonNull)
                .filter(date -> !date.isBefore(today))
                .min(Comparator.naturalOrder())
                .orElse(null);
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
        return java.util.stream.Stream.of(today.getYear() - 1, today.getYear(), today.getYear() + 1)
                .map(year -> convertLunarToSolar(year, commonAnniversary.getMonth(), commonAnniversary.getDay()))
                .filter(java.util.Objects::nonNull)
                .filter(date -> !date.isBefore(today))
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    // 생성/수정 시 반복하지 않는 기념일이 과거 날짜인지 검증
    private void validatePastDateNotRepeated(LocalDate anniversaryDate, Boolean isRepeated) {
        if (Boolean.TRUE.equals(isRepeated)) {
            return;
        }
        if (anniversaryDate.isBefore(LocalDate.now())) {
            throw new AnniversaryException(AnniversaryErrorCode.PAST_DATE_NOT_REPEATED);
        }
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

        validatePastDateNotRepeated(request.anniversaryDate(), request.isRepeated());

        Anniversary anniversary = AnniversaryConverter.toAnniversary(member, request);
        Anniversary savedAnniversary = anniversaryRepository.save(anniversary);

        applicationEventPublisher.publishEvent(new AnniversaryCreatedEvent(savedAnniversary.getId()));

        return AnniversaryConverter.toCreateAnniversary(savedAnniversary);
    }

    @Transactional
    public AnniversaryResDTO.UpdateAnniversary updateAnniversary(Long memberId, Long anniversaryId, AnniversaryReqDTO.Update request) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_FOUND));

        validatePastDateNotRepeated(request.anniversaryDate(), request.isRepeated());

        Anniversary anniversary = getOwnedAnniversary(memberId, anniversaryId);

        boolean titleChanged = !Objects.equals(anniversary.getTitle(), request.title());
        boolean memoChanged = !Objects.equals(anniversary.getMemo(), request.memo());

        anniversary.update(
                request.title(),
                request.anniversaryDate(),
                request.isLunar(),
                request.isRepeated(),
                request.sevenDaysAlarmEnabled(),
                request.dayAlarmEnabled(),
                request.memo()
        );

        applicationEventPublisher.publishEvent(new AnniversaryUpdatedEvent(anniversaryId, titleChanged, memoChanged));

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
            throw new AnniversaryException(AnniversaryErrorCode.ANNIVERSARY_NOT_FOUND);
        }

        notificationRepository.deleteAllByAnniversaryIdIn(distinctIds);
        anniversaryRepository.deleteAll(anniversaries);
    }

    @Transactional
    public void deleteExpiredNonRepeatingAnniversaries() {
        LocalDate today = LocalDate.now();

        List<Anniversary> expiredSolarAnniversaries =
                anniversaryRepository.findAllByIsRepeatedFalseAndIsLunarFalseAndAnniversaryDateBefore(today);

        List<Anniversary> expiredLunarAnniversaries =
                anniversaryRepository.findAllByIsRepeatedFalseAndIsLunarTrue().stream()
                        .filter(anniversary -> isExpiredNonRepeatingLunar(anniversary, today))
                        .toList();

        List<Anniversary> expiredAnniversaries = java.util.stream.Stream.concat(
                        expiredSolarAnniversaries.stream(), expiredLunarAnniversaries.stream())
                .toList();

        if (expiredAnniversaries.isEmpty()) {
            return;
        }
        List<Long> expiredIds = expiredAnniversaries.stream().map(Anniversary::getId).toList();
        notificationRepository.deleteAllByAnniversaryIdIn(expiredIds);
        anniversaryRepository.deleteAll(expiredAnniversaries);
    }

    private Anniversary getOwnedAnniversary(Long memberId, Long anniversaryId) {
        Anniversary anniversary = anniversaryRepository.findById(anniversaryId)
                .orElseThrow(() -> new AnniversaryException(AnniversaryErrorCode.ANNIVERSARY_NOT_FOUND));
        if (!anniversary.getMember().getId().equals(memberId)) {
            throw new AnniversaryException(AnniversaryErrorCode.ANNIVERSARY_NOT_FOUND);
        }
        return anniversary;
    }

    private boolean isExpiredNonRepeatingLunar(Anniversary anniversary, LocalDate today) {
        LocalDate solarDate = convertLunarToSolar(
                anniversary.getAnniversaryDate().getYear(),
                anniversary.getAnniversaryDate().getMonthValue(),
                anniversary.getAnniversaryDate().getDayOfMonth());
        return solarDate != null && solarDate.isBefore(today);
    }
}