package com.umc.halo.global.rateLimit;

public record RateLimitKey(
        Long memberId,
        AiRateLimitType aiRateLimitType
) {}
