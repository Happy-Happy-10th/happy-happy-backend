package com.happyhappy.backend.calendar.controller;

import com.happyhappy.backend.calendar.dto.AiEventDto;
import com.happyhappy.backend.calendar.dto.EventDto.EventRequest;
import com.happyhappy.backend.calendar.dto.EventDto.EventResponse;
import com.happyhappy.backend.calendar.service.AiEventService;
import com.happyhappy.backend.calendar.service.EventService;
import com.happyhappy.backend.calendar.service.HolidayApiService;
import com.happyhappy.backend.common.response.ApiResponseCode;
import com.happyhappy.backend.common.response.ApiResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "캘린더 일정 생성", description = "사용자 일정 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponseMessage> createEvent(
            @RequestBody EventRequest request) {
        EventResponse result = eventService.createEvent(request);
        return ResponseEntity.ok(ApiResponseMessage.success(result, "캘린더 일정이 생성되었습니다."));
    }

    @Operation(summary = "캘린더 일정 수정", description = "사용자 일정 수정합니다.")
    @PutMapping("/{eventId}")
    public ResponseEntity<ApiResponseMessage> updateEvent(
            @PathVariable Long eventId,
            @RequestBody EventRequest request) {
        EventResponse result = eventService.updateEvent(eventId, request);
        return ResponseEntity.ok(ApiResponseMessage.success(result, "캘린더 일정이 수정되었습니다."));
    }

    @Operation(summary = "캘린더 일정 삭제", description = "사용자 일정 삭제합니다.")
    @DeleteMapping("/{eventId}")
    public ResponseEntity<ApiResponseMessage> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.ok(ApiResponseMessage.success(null, "캘린더 일정이 삭제되었습니다."));
    }

    @Operation(summary = "캘린더 일정 조회", description = "사용자 일정 연도별, 반복주기별 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponseMessage> getEventsByYear(
            @RequestParam Long calendarId,
            @RequestParam int year) {
        List<EventResponse> results = eventService.getEventsByYear(calendarId, year);
        return ResponseEntity.ok(ApiResponseMessage.success(results, "캘린더 일정 목록을 조회했습니다."));
    }

    @Operation(summary = "캘린더 공휴일 일정 조회", description = "공휴일 연도별 조회합니다.")
    @GetMapping("/holidays")
    public ResponseEntity<ApiResponseMessage> getHolidays(
            @RequestParam int year) {
        List<EventResponse> holidays = IntStream.rangeClosed(1, 12)
                .boxed()
                .flatMap(m -> holidayApiService.getHolidays(year, m).stream())
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponseMessage.success(holidays, "공휴일 목록을 조회했습니다."));
    }

    @PostMapping("/ai-event")
    public ResponseEntity<ApiResponseMessage> saveAiEvent(@RequestBody AiEventDto request) {
        aiEventService.saveAiEvent(request);
        ApiResponseMessage res = new ApiResponseMessage(
                ApiResponseCode.COMMON_SUCCESS_000001,
                "AI 추천 일정 저장 했습니다."
        );
        return ResponseEntity.ok(res);
    }

}
