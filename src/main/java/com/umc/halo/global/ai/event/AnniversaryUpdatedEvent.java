package com.umc.halo.global.ai.event;

public record AnniversaryUpdatedEvent(
        Long anniversaryId,
        boolean titleChanged,
        boolean memoChanged
) {}
