package com.umc.halo.domain.content.storybook.enums;

public enum StorybookStatus {
    NOT_STARTED,   // 아직 시작 안 함
    IN_PROGRESS,   // 진행중 (오늘 진행 가능한 장 있음)
    TODAY_DONE,    // 오늘 진행할 몫은 다 함 (일일 한도)
    COMPLETED      // 10장 다 완료
}