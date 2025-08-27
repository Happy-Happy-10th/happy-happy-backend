package com.happyhappy.backend.calendar.service;

import com.happyhappy.backend.calendar.dto.EventDto.EventRequest;
import com.happyhappy.backend.calendar.dto.EventDto.EventResponse;
import com.happyhappy.backend.calendar.enums.RepeatType;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventResponse createEvent(EventRequest request);

    EventResponse updateEvent(Long eventId, EventRequest request);

    void deleteEvent(Long eventId);

    List<EventResponse> getEventsByYear(Long calendarId, int year);

    void validateRepeatPeriod(LocalDateTime start, LocalDateTime end, RepeatType repeatType);

    void validateDateOrder(LocalDateTime start, LocalDateTime end);
}