package com.umc.halo.domain.notification.service;

import com.umc.halo.domain.member.entity.Member;
import com.umc.halo.domain.notification.converter.AnniversaryConverter;
import com.umc.halo.domain.notification.dto.AnniversaryReqDTO;
import com.umc.halo.domain.notification.dto.AnniversaryResDTO;
import com.umc.halo.domain.notification.entity.Anniversary;
import com.umc.halo.domain.notification.entity.CommonAnniversary;
import com.umc.halo.domain.notification.exception.AnniversaryErrorCode;
import com.umc.halo.domain.notification.exception.AnniversaryException;
import com.umc.halo.domain.notification.repository.AnniversaryRepository;
import com.umc.halo.domain.notification.repository.CommonAnniversaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AnniversaryService {

    private final AnniversaryRepository anniversaryRepository;
    private final CommonAnniversaryRepository commonAnniversaryRepository;

    @Transactional(readOnly = true)
    public AnniversaryResDTO.GetAnniversaries getAnniversaries(Member member) {
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

        // 음력 기념일(추석, 설날 등)은 연도별 환산 로직이 없어 이번 PR에서는 다가오는 기념일 D-day 계산 대상에서 제외
        // (기본 기념일 목록에는 그대로 노출됨)
        List<AnniversaryResDTO.Upcoming> upcomingFromCommonAnniversaries = commonAnniversaryEntities.stream()
                .filter(commonAnniversary -> !Boolean.TRUE.equals(commonAnniversary.getIsLunar()))
                .map(commonAnniversary -> {
                    LocalDate nextOccurrence = resolveNextOccurrence(commonAnniversary, today);
                    return AnniversaryResDTO.Upcoming.builder()
                            .anniversaryId(null)
                            .title(commonAnniversary.getTitle())
                            .anniversaryDate(nextOccurrence)
                            .dDay((int) ChronoUnit.DAYS.between(today, nextOccurrence))
                            .build();
                })
                .toList();

        return Stream.concat(upcomingFromMyAnniversaries.stream(), upcomingFromCommonAnniversaries.stream())
                .sorted(Comparator.comparing(AnniversaryResDTO.Upcoming::dDay))
                .toList();
    }

    private LocalDate resolveNextOccurrence(Anniversary anniversary, LocalDate today) {
        if (!Boolean.TRUE.equals(anniversary.getIsRepeated())) {
            LocalDate date = anniversary.getAnniversaryDate();
            return date.isBefore(today) ? null : date;
        }
        LocalDate thisYear = anniversary.getAnniversaryDate().withYear(today.getYear());
        return thisYear.isBefore(today) ? thisYear.plusYears(1) : thisYear;
    }

    private LocalDate resolveNextOccurrence(CommonAnniversary commonAnniversary, LocalDate today) {
        LocalDate thisYear = LocalDate.of(today.getYear(), commonAnniversary.getMonth(), commonAnniversary.getDay());
        return thisYear.isBefore(today) ? thisYear.plusYears(1) : thisYear;
    }

    @Transactional
    public AnniversaryResDTO.CreateAnniversary createAnniversary(Member member, AnniversaryReqDTO.Create request) {
        Anniversary anniversary = AnniversaryConverter.toAnniversary(member, request);
        Anniversary savedAnniversary = anniversaryRepository.save(anniversary);
        return AnniversaryConverter.toCreateAnniversary(savedAnniversary);
    }

    @Transactional
    public AnniversaryResDTO.UpdateAnniversary updateAnniversary(Member member, Long anniversaryId, AnniversaryReqDTO.Update request) {
        Anniversary anniversary = getOwnedAnniversary(member, anniversaryId);
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
    public void deleteAnniversaries(Member member, List<Long> anniversaryIds) {
        List<Anniversary> anniversaries = anniversaryRepository.findAllById(anniversaryIds);

        if (anniversaries.size() != anniversaryIds.size()) {
            throw new AnniversaryException(AnniversaryErrorCode.ANNIVERSARY_NOT_FOUND);
        }

        boolean hasUnauthorized = anniversaries.stream()
                .anyMatch(anniversary -> !anniversary.getMember().getId().equals(member.getId()));
        if (hasUnauthorized) {
            throw new AnniversaryException(AnniversaryErrorCode.ANNIVERSARY_ACCESS_DENIED);
        }

        anniversaryRepository.deleteAll(anniversaries);
    }

    private Anniversary getOwnedAnniversary(Member member, Long anniversaryId) {
        Anniversary anniversary = anniversaryRepository.findById(anniversaryId)
                .orElseThrow(() -> new AnniversaryException(AnniversaryErrorCode.ANNIVERSARY_NOT_FOUND));
        if (!anniversary.getMember().getId().equals(member.getId())) {
            throw new AnniversaryException(AnniversaryErrorCode.ANNIVERSARY_ACCESS_DENIED);
        }
        return anniversary;
    }
}
