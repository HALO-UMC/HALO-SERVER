package com.umc.halo.global.seed;

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

    private final MemberSeeder memberSeeder;
    private final ChapterSeeder chapterSeeder;
    private final StorybookSeeder storybookSeeder;
    private final TagSeeder tagSeeder;

    @Override
    public void run(String... args) throws Exception {

        log.info("로컬 데이터 초기화");

        memberSeeder.seed();
        chapterSeeder.seed();
        storybookSeeder.seed();
        tagSeeder.seed();

    }

}
