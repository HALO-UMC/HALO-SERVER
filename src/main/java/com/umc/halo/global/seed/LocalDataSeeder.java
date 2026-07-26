package com.umc.halo.global.seed;

import com.umc.halo.domain.member.repository.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.boot.*;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.*;

@Component
@Profile("local")
@RequiredArgsConstructor
@Slf4j
public class LocalDataSeeder implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final MemberSeeder memberSeeder;
    private final ChapterSeeder chapterSeeder;
    private final StorybookSeeder storybookSeeder;
    private final TagSeeder tagSeeder;
    private final RecordSeeder recordSeeder;
    private final TermSeeder termSeeder;
    private final NotificationSeeder notificationSeeder;
    private final SettingSeeder settingSeeder;

    @Override
    public void run(String... args) throws Exception {

        if (memberRepository.count() > 0) {
            log.info("이미 시딩된 데이터가 있어 로컬 데이터 초기화를 스킵합니다.");
            return;
        }

        log.info("로컬 데이터 초기화");

        memberSeeder.seed();
        chapterSeeder.seed();
        storybookSeeder.seed();
        tagSeeder.seed();
        recordSeeder.seed();
        termSeeder.seed();
        notificationSeeder.seed();
        settingSeeder.seed();

    }

}
