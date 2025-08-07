package com.happyhappy.backend.calendar.service;

import com.happyhappy.backend.calendar.domain.CalendarDto.CalendarResponse;
import com.happyhappy.backend.calendar.domain.CalendarDto.MonthlyCalendarResponse;
import java.util.UUID;

public interface CalendarService {

    MonthlyCalendarResponse getMonthlyCalendar(Long calendarId, int year, int month);

    MonthlyCalendarResponse getCurrentMonthCalendar(Long calendarId);

    CalendarResponse getCalendarByMemberId(UUID memberId);
}
