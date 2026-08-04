package com.umc.halo.domain.notification.scheduler;

import com.umc.halo.domain.notification.service.AnniversaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnniversaryScheduler {

    private final AnniversaryService anniversaryService;

    // 매일 자정에 지난 비반복 기념일 자동 삭제
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredNonRepeatingAnniversaries() {
        anniversaryService.deleteExpiredNonRepeatingAnniversaries();
        log.info("비반복 기념일 자동 삭제 스케줄러 실행 완료");
    }
}