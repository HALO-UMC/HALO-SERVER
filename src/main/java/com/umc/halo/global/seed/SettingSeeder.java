package com.umc.halo.global.seed;

import com.umc.halo.domain.member.entity.*;
import com.umc.halo.domain.member.repository.*;
import com.umc.halo.domain.setting.entity.*;
import com.umc.halo.domain.setting.repository.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class SettingSeeder {
    private final BgmRepository bgmRepository;
    private final MemberSettingRepository memberSettingRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public List<Bgm> seedBgm() {
        List<Bgm> bgms = List.of(
                Bgm.builder()
                        .title("산들 바람")
                        .fileName("https://example.com/bgm1.mp3")
                        .build(),
                Bgm.builder()
                        .title("따뜻한 오후")
                        .fileName("https://example.com/bgm2.mp3")
                        .build()
        );

        List<Bgm> savedBgms = bgmRepository.saveAll(bgms);
        log.info("Bgm {}건 시딩 완료", savedBgms.size());

        return savedBgms;
    }

    @Transactional
    public List<MemberSetting> seedMemberSetting(List<Bgm> bgms) {
        Map<String, Member> member = memberRepository.findAll().stream()
                .collect(Collectors.toMap(Member::getName, Function.identity(), (a, b) -> a));

        List<MemberSetting> memberSettings = List.of(
                MemberSetting.builder()
                        .member(member.get("김하로"))
                        .bgm(bgms.get(0))
                        .build(),
                MemberSetting.builder()
                        .member(member.get("이온"))
                        .bgm(bgms.get(1))
                        .build()
        );

        List<MemberSetting> savedMemberSettings = memberSettingRepository.saveAll(memberSettings);
        log.info("MemberSetting {}건 시딩 완료", savedMemberSettings.size());

        return savedMemberSettings;
    }

    @Transactional
    public void seed() {
        List<Bgm> bgms = seedBgm();
        seedMemberSetting(bgms);
    }
}