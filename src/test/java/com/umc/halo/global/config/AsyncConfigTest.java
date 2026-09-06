package com.umc.halo.global.config;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AsyncConfig.asyncExecutor()가 application.yml의 async.executor.* 설정값대로
 * ThreadPoolTaskExecutor를 만드는지, 거부 정책이 CallerRunsPolicy인지 검증.
 * @Value로 주입되는 파라미터는 메서드를 직접 호출하면서 리터럴 값으로 대체한다
 * (Spring 컨테이너 없이도 순수 자바 메서드 호출로 테스트 가능).
 */
class AsyncConfigTest {

    private final AsyncConfig asyncConfig = new AsyncConfig();

    @Test
    void asyncExecutor는_설정값대로_스레드풀_속성을_구성한다() {
        ThreadPoolTaskExecutor executor = asyncConfig.asyncExecutor(4, 8, 50, 60, 20);

        assertThat(executor.getCorePoolSize()).isEqualTo(4);
        assertThat(executor.getMaxPoolSize()).isEqualTo(8);
        assertThat(executor.getQueueCapacity()).isEqualTo(50);
        assertThat(executor.getKeepAliveSeconds()).isEqualTo(60);
        assertThat(executor.getThreadNamePrefix()).isEqualTo("halo-async-");
    }

    @Test
    void asyncExecutor는_큐가_가득_차면_호출_스레드가_직접_실행하는_CallerRunsPolicy를_쓴다() {
        ThreadPoolTaskExecutor executor = asyncConfig.asyncExecutor(4, 8, 50, 60, 20);
        executor.initialize();

        try {
            assertThat(executor.getThreadPoolExecutor().getRejectedExecutionHandler())
                    .isInstanceOf(ThreadPoolExecutor.CallerRunsPolicy.class);
        } finally {
            executor.shutdown();
        }
    }
}