package com.happyhappy.backend.calendar.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EventColor {
    YOTEYO_RED("yoteyoRed"),
    YOTEYO_ORANGE("yoteyoOrange"),
    YOTEYO_YELLOW("yoteyoYellow"),
    YOTEYO_GREEN("yoteyoGreen"),
    YOTEYO_EMERALD("yoteyoEmerald"),
    YOTEYO_SKY("yoteyoSky"),
    YOTEYO_BLUE("yoteyoBlue"),
    YOTEYO_PURPLE("yoteyoPurple"),
    YOTEYO_PINK("yoteyoPink"),
    YOTEYO_NAVY("yoteyoNavy"),
    YOTEYO_GRAY("yoteyoGray");
    private final String code;

    EventColor(String code) {
        this.code = code;
    }

    @JsonCreator
    public static EventColor fromCode(String code) {
        for (EventColor ec : EventColor.values()) {
            if (ec.code.equals(code)) {
                return ec;
            }
        }
        throw new IllegalArgumentException("알 수 없는 EventColor: " + code);
    }

    @JsonValue
    public String getCode() {
        return code;
    }
}
