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
    YOTEYO_BLACK("yoteyoBlack");

    private final String code;

    EventColor(String code) {
        this.code = code;
    }

    @JsonCreator
    public static EventColor fromCode(String code) {
        for (EventColor ec : values()) {
            if (ec.code.equals(code)) {
                return ec;
            }
        }
        throw new IllegalArgumentException("Unknown EventColor: " + code);
    }

    @JsonValue
    public String getCode() {
        return code;
    }
}
