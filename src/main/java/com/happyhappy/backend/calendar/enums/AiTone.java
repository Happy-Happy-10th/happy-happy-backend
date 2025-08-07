package com.happyhappy.backend.calendar.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AiTone {
    PLAIN("담백한 말투");

    private final String displayName;

    public static AiTone getDefault() {
        return PLAIN;
    }
}
