package com.happyhappy.backend.calendar.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TimeFormat {
    TWELVE_HOUR("12시간제", "hh:mm a"),
    TWENTY_FOUR_HOUR("24시간제", "HH:mm");

    private final String displayName;
    private final String pattern;

    public static TimeFormat getDefault() {
        return TWENTY_FOUR_HOUR;
    }
}
