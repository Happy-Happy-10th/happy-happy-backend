package com.happyhappy.backend.calendar.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WeekStartDay {
    MONDAY,
    SUNDAY;

    public static WeekStartDay getDefault() {
        return MONDAY;
    }
}
