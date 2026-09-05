package com.umc.halo.global.rateLimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * AI API Rate Limit.
 * 현재 EC2 단일 인스턴스 환경을 기준으로 메모리 기반 Bucket을 사용한다.
 * 멀티 인스턴스 환경에서는 Redis 기반 Bucket4j 저장소로 교체해야 한다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitService {

    private final Cache<RateLimitKey, Bucket> buckets =
            Caffeine.newBuilder()
                    .expireAfterAccess(Duration.ofDays(1))
                    .maximumSize(100_000)
                    .build();

    public boolean tryConsume(Long memberId, AiRateLimitType aiRateLimitType) {

        RateLimitKey rateLimitKey = new RateLimitKey(memberId, aiRateLimitType);

        Bucket bucket = buckets.get(rateLimitKey, key -> createBucket(aiRateLimitType));

        boolean allowed = bucket.tryConsume(1);

        if (!allowed) {
            log.warn("AI Rate Limit 초과 memberId={}, api={}, remaining={}", memberId, aiRateLimitType, bucket.getAvailableTokens());
        }

        return allowed;
    }

    private Bucket createBucket(AiRateLimitType aiRateLimitType) {

        Bandwidth minuteLimit;
        Bandwidth dayLimit;

        switch (aiRateLimitType) {
            case CHAPTER_SUMMARY -> {
                    minuteLimit = Bandwidth.builder()
                            .capacity(3)
                            .refillIntervally(3, Duration.ofMinutes(1))
                            .build();

                    dayLimit = Bandwidth.builder()
                            .capacity(10)
                            .refillIntervally(10, Duration.ofDays(1))
                            .build();
            }

            case ANNIVERSARY_MESSAGE -> {
                    minuteLimit = Bandwidth.builder()
                            .capacity(5)
                            .refillIntervally(5, Duration.ofMinutes(1))
                            .build();

                    dayLimit = Bandwidth.builder()
                        .capacity(30)
                        .refillIntervally(30, Duration.ofDays(1))
                        .build();
            }

            default -> throw new IllegalStateException("Unknown AI RateLimitType");
        }

        return Bucket.builder()
                .addLimit(minuteLimit)
                .addLimit(dayLimit)
                .build();
    }
}
