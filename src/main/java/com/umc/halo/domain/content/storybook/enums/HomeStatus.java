package com.umc.halo.domain.content.storybook.enums;

public enum HomeStatus {
    NO_STORYBOOK,          // 진행중인 스토리북 없음
    IN_PROGRESS,           // 진행중인 스토리북 1개
    MULTIPLE_IN_PROGRESS,  // 진행중인 스토리북 여러 개
    ALL_COMPLETED_TODAY    // 진행중인 스토리북은 있지만 전부 오늘 몫 완료
}