package com.happyhappy.backend.calendar.service;

import com.happyhappy.backend.calendar.domain.Calendar;
import com.happyhappy.backend.calendar.domain.Event;
import com.happyhappy.backend.calendar.dto.AiEventDto;
import com.happyhappy.backend.calendar.enums.EventColor;
import com.happyhappy.backend.calendar.repository.CalendarRepository;
import com.happyhappy.backend.calendar.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiEventServiceImpl implements AiEventService {

    private final EventRepository eventRepository;
    private final CalendarRepository calendarRepository;

    @Override
    public void saveAiEvent(AiEventDto dto) {

        System.out.println(">>>> [Service] calendarId = " + dto.getCalendarId());

        Calendar calendar = calendarRepository.findById(dto.getCalendarId())
                .orElseThrow(() -> new IllegalArgumentException("캘린더가 존재하지 않습니다."));

        Event event = Event.builder()
                .title(dto.getTitle())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .allDay(true)
                .memo(dto.getMemo())
                .locate(dto.getLocation())
                .homepageUrl(dto.getHomepageUrl())
                .detailPageUrl(dto.getDetailPageUrl())
                .confidence(dto.getConfidence())
                .color(EventColor.YOTEYO_PURPLE)
                .isHoliday(false)
                .calendar(calendar)
                .build();

        eventRepository.save(event);
    }
}

