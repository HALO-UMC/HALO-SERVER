package com.umc.halo.global.seed;

import com.umc.halo.domain.member.entity.*;
import com.umc.halo.domain.member.repository.*;
import com.umc.halo.domain.notification.entity.*;
import com.umc.halo.domain.notification.enums.*;
import com.umc.halo.domain.notification.repository.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationSeeder {
    private final AnniversaryRepository anniversaryRepository;
    private final CommonAnniversaryRepository commonAnniversaryRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public List<CommonAnniversary> seedCommonAnniversary() {
        List<CommonAnniversary> commonAnniversaries = List.of(
                CommonAnniversary.builder()
                        .title("어버이날")
                        .month(5)
                        .day(8)
                        .isLunar(false)
                        .build(),
                CommonAnniversary.builder()
                        .title("추석")
                        .month(8)
                        .day(15)
                        .isLunar(true)
                        .build(),
                CommonAnniversary.builder()
                        .title("설날")
                        .month(1)
                        .day(1)
                        .isLunar(true)
                        .build()
        );

        List<CommonAnniversary> savedCommonAnniversaries = commonAnniversaryRepository.saveAll(commonAnniversaries);
        log.info("CommonAnniversary {}건 시딩 완료", savedCommonAnniversaries.size());

        return savedCommonAnniversaries;
    }

    @Transactional
    public List<Anniversary> seedAnniversary() {
        Map<String, Member> member = memberRepository.findAll().stream()
                .collect(Collectors.toMap(Member::getName, Function.identity(), (a, b) -> a));

        List<Anniversary> anniversaries = List.of(
                Anniversary.builder()
                        .member(member.get("김하로"))
                        .title("아버지 생신")
                        .anniversaryDate(LocalDate.of(2026, 9, 12))
                        .target(Target.FATHER)
                        .memo("아버지 생신! 열심히 준비해야겠다!")
                        .build(),
                Anniversary.builder()
                        .member(member.get("이온"))
                        .title("어머니 생신")
                        .anniversaryDate(LocalDate.of(2026, 11, 20))
                        .target(Target.MOTHER)
                        .memo("어머니 생신! 이번에는 더 특별히 준비해야지!")
                        .build()
        );

        List<Anniversary> savedAnniversaries = anniversaryRepository.saveAll(anniversaries);
        log.info("Anniversary {}건 시딩 완료", savedAnniversaries.size());

        return savedAnniversaries;
    }

    @Transactional
    public void seed() {
        seedCommonAnniversary();
        seedAnniversary();
    }
}
