package com.happyhappy.backend.calendar.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ColorBlindMode {
    NOMAL("일반", "#defualt"),
    RED_GREEN("적녹색약 모드", "#red-green"),
    BLUE_YELLOW("청색약 모드", "#blue-yellow");

    private final String displayName;
    private final String colorScheme;

    public static ColorBlindMode getDefault() {
        return NOMAL;
    }
}
