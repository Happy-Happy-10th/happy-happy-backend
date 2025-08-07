package com.happyhappy.backend.calendar.service;

import com.happyhappy.backend.calendar.domain.CalendarDto.SettingsResponse;
import com.happyhappy.backend.calendar.enums.AiTone;
import com.happyhappy.backend.calendar.enums.ColorBlindMode;
import com.happyhappy.backend.calendar.enums.TimeFormat;
import com.happyhappy.backend.calendar.enums.WeekStartDay;
import com.happyhappy.backend.calendar.enums.WeekendType;
import java.time.DayOfWeek;
import java.util.List;

public interface CalendarSettingService {

    SettingsResponse getSettings(Long calendarId);

    void updateWeekStartDay(Long calendarId, WeekStartDay weekStartDay);

    void updateColorBlindMode(Long calendarId, ColorBlindMode colorBlindMode);

    void updateTimeFormat(Long calendarId, TimeFormat timeFormat);

    void updateWeekendSettings(Long calendarId, WeekendType weekendType,
            List<DayOfWeek> weekendDays);

    void updateAiTone(Long calendarId, AiTone aiTone);

    void updateAiSearchRegion(Long calendarId, String sidoCode, String sigunguCode);

    /**
     * 설정 초기화 버튼 추가 시 기능 추가
     */
//    void resetToDefault(Long calendarId);
}
