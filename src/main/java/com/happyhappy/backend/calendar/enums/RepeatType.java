package com.happyhappy.backend.calendar.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.LocalDateTime;

public enum RepeatType {
    DAY("day"),
    WEEK("week"),
    MONTH("month"),
    YEAR("year");

    private final String code;

    RepeatType(String code) {
        this.code = code;
    }

    @JsonCreator
    public static RepeatType fromCode(String code) {
        for (RepeatType rt : values()) {
            if (rt.code.equalsIgnoreCase(code)) {
                return rt;
            }
        }
        throw new IllegalArgumentException("Unknown RepeatType: " + code);
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public LocalDateTime next(LocalDateTime time) {
        return switch (this) {
            case DAY -> time.plusDays(1);
            case WEEK -> time.plusWeeks(1);
            case MONTH -> time.plusMonths(1);
            case YEAR -> time.plusYears(1);
        };
    }
}
