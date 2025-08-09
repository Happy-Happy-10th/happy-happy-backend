package com.happyhappy.backend.calendar.controller;

import com.happyhappy.backend.calendar.dto.AiEventDto;
import com.happyhappy.backend.calendar.dto.EventDto;
import com.happyhappy.backend.calendar.dto.EventDto.EventResponse;
import com.happyhappy.backend.calendar.service.AiEventService;
import com.happyhappy.backend.calendar.service.EventService;
import com.happyhappy.backend.calendar.service.HolidayApiService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Event")
@RestController
@RequestMapping("/calendar/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final HolidayApiService holidayApiService;
    private final AiEventService aiEventService;

    // 사용자 일정 생성
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @RequestBody EventDto.EventRequest request) {
        return ResponseEntity.ok(eventService.createEvent(request));
    }

    // 사용자 일정 수정
    @PutMapping("/{eventId}")
    public ResponseEntity<EventDto.EventResponse> updateEvent(
            @PathVariable Long eventId,
            @RequestBody EventDto.EventRequest request) {
        return ResponseEntity.ok(eventService.updateEvent(eventId, request));
    }

    // 사용자 일정 삭제
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }


    // 사용자 연도별 일정 조회 + 반복주기 별 조회
    @GetMapping
    public ResponseEntity<List<EventResponse>> getEventsByYear(
            @RequestParam Long calendarId,
            @RequestParam int year) {
        return ResponseEntity.ok(
                eventService.getEventsByYear(calendarId, year)
        );
    }

    // 공휴일 연도별 전체 조회
    @GetMapping("/holidays")
    public ResponseEntity<List<EventResponse>> getHolidays(
            @RequestParam int year) {
        List<EventResponse> holidays = IntStream.rangeClosed(1, 12)  // 1부터 12월까지
                .boxed()
                .flatMap(m -> holidayApiService.getHolidays(year, m).stream())
                .collect(Collectors.toList());

        return ResponseEntity.ok(holidays);
    }

    @PostMapping("/ai-event")
    public ResponseEntity<String> saveAiEvent(@RequestBody AiEventDto request) {
        aiEventService.saveAiEvent(request);
        return ResponseEntity.ok("AI 일정 저장 성공");
    }


}
