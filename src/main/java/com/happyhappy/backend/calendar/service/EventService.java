package com.happyhappy.backend.calendar.service;

import com.happyhappy.backend.calendar.dto.EventDto.EventRequest;
import com.happyhappy.backend.calendar.dto.EventDto.EventResponse;
import java.util.List;

public interface EventService {

    EventResponse createEvent(EventRequest request);

    EventResponse updateEvent(Long eventId, EventRequest request);

    void deleteEvent(Long eventId);

    List<EventResponse> getEventsByYear(Long calendarId, int year);
}