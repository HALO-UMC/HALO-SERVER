package com.umc.halo.domain.content.storybook.enums;

public enum ChapterViewStatus {
    COMPLETED,     // 완료한 장
    TODAY,         // 오늘 진행 가능한 장
    TODAY_LOCKED,  // 오늘 한도 초과로 잠긴 장
    LOCKED         // 아직 순서가 안 된 장
}