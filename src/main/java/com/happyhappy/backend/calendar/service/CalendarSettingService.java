package com.happyhappy.backend.calendar.service;

import com.happyhappy.backend.calendar.domain.CalendarDto.SettingsResponse;
import com.happyhappy.backend.calendar.enums.TimeFormat;
import com.happyhappy.backend.calendar.enums.WeekStartDay;

public interface CalendarSettingService {

    SettingsResponse getSettings(Long calendarId);

    void updateWeekStartDay(Long calendarId, WeekStartDay weekStartDay);

    void updateTimeFormat(Long calendarId, TimeFormat timeFormat);

    void updateAiSearchRegion(Long calendarId, String sidoCode, String sigunguCode);

    /**
     * 설정 초기화 버튼 추가 시 기능 추가
     */
//    void resetToDefault(Long calendarId);
}
