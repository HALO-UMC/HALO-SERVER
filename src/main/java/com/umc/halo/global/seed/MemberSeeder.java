package com.umc.halo.global.seed;

import com.umc.halo.domain.content.storybook.entity.StorybookCharacter;
import com.umc.halo.domain.content.storybook.repository.StorybookCharacterRepository;
import com.umc.halo.domain.member.entity.*;
import com.umc.halo.domain.member.enums.*;
import com.umc.halo.domain.member.repository.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.time.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Slf4j
@RequiredArgsConstructor
public class MemberSeeder {
    private final MemberRepository memberRepository;
    private final StorybookCharacterRepository storybookCharacterRepository;

    @Transactional
    public List<Member> seed() {

        List<StorybookCharacter> characters = storybookCharacterRepository.findAll();

        List<Member> members = List.of(
                Member.builder()
                        .name("김하로")
                        .provider(Provider.KAKAO)
                        .providerId("kakao-0001")
                        .gender(Gender.FEMALE)
                        .birthDate(LocalDate.of(2000, 5, 12))
                        .onboardingCompleted(true)
                        .storybookCharacter(pickRandomCharacter(characters))
                        .build(),
                Member.builder()
                        .name("이온")
                        .provider(Provider.GOOGLE)
                        .providerId("google-0001")
                        .gender(Gender.MALE)
                        .birthDate(LocalDate.of(1993, 8, 23))
                        .onboardingCompleted(true)
                        .storybookCharacter(pickRandomCharacter(characters))
                        .build(),
                Member.builder()
                        .name("송하루")
                        .gender(Gender.MALE)
                        .birthDate(LocalDate.of(2011, 4, 5))
                        .build()
        );
        List<Member> savedMembers = memberRepository.saveAll(members);
        log.info("Member {}건 시딩 완료", savedMembers.size());

        return savedMembers;
    }

    private StorybookCharacter pickRandomCharacter(List<StorybookCharacter> characters) {
        if (characters.isEmpty()) {
            throw new IllegalStateException("배정 가능한 StorybookCharacter가 없습니다. StorybookSeeder가 먼저 실행되었는지 확인하세요.");
        }
        return characters.get(ThreadLocalRandom.current().nextInt(characters.size()));
    }
}
