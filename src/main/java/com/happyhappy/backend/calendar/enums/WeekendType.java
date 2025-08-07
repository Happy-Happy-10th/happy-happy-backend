package com.happyhappy.backend.calendar.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WeekendType {
    DEFAULT("기본 - 토일 고정"),
    CUSTOM("사용자 선택");

    private final String displayName;

    public static WeekendType getDefault() {
        return DEFAULT;
    }
}
