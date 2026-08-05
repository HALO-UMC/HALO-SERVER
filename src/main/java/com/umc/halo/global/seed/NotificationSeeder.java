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
                        .title("아버지의 날")
                        .month(3)
                        .day(19)
                        .isLunar(false)
                        .memo("오늘은 아버지께 평소 전하지 못했던 마음을 건네보세요.")
                        .build(),
                CommonAnniversary.builder()
                        .title("어버이날")
                        .month(5)
                        .day(8)
                        .isLunar(false)
                        .memo("늘 곁을 지켜준 부모님께 감사한 마음을 전해보세요.")
                        .build(),
                CommonAnniversary.builder()
                        .title("세계 부모의 날")
                        .month(6)
                        .day(1)
                        .isLunar(false)
                        .memo("부모님의 사랑과 헌신을 다시 한번 떠올려보는 날이에요.")
                        .build(),
                CommonAnniversary.builder()
                        .title("세계 사진의 날")
                        .month(8)
                        .day(19)
                        .isLunar(false)
                        .memo("부모님과 함께한 소중한 순간을 오늘 한 장 남겨보세요.")
                        .build(),
                CommonAnniversary.builder()
                        .title("세계 돌봄과 지원의 날")
                        .month(10)
                        .day(29)
                        .isLunar(false)
                        .memo("서로를 아끼고 돌봐온 마음을 따뜻하게 나눠보세요.")
                        .build(),
                CommonAnniversary.builder()
                        .title("어머니의 날")
                        .month(12)
                        .day(22)
                        .isLunar(false)
                        .memo("오늘은 어머니께 따뜻한 마음 한마디를 전해보세요.")
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
                        .memo("아버지 생신! 열심히 준비해야겠다!")
                        .build(),
                Anniversary.builder()
                        .member(member.get("이온"))
                        .title("어머니 생신")
                        .anniversaryDate(LocalDate.of(2026, 11, 20))
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
