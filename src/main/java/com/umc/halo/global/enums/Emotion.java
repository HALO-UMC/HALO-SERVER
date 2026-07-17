package com.umc.halo.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Emotion {
    GRATEFUL("감사했어요"),    // 감사했어요
    SAD("슬펐어요"),         // 슬펐어요
    THOUGHTFUL("생각이 많아요"),  // 생각이 많아요
    ANGRY("화가 났어요"),       // 화가 났어요
    AWKWARD("어색했어요"),     // 어색했어요
    HAPPY("즐거웠어요");       // 즐거웠어요

    private final String description;
}