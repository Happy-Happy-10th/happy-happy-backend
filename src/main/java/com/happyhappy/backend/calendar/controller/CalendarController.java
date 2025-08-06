package com.happyhappy.backend.calendar.controller;

import com.happyhappy.backend.calendar.domain.CalendarDto.CalendarResponse;
import com.happyhappy.backend.calendar.domain.CalendarDto.MonthlyCalendarResponse;
import com.happyhappy.backend.calendar.service.CalendarService;
import com.happyhappy.backend.common.response.ApiResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calendars")
@Tag(name = "calendar")
public class CalendarController {

    private final CalendarService calendarService;

    @Operation(summary = "월별 캘린더 조회", description = "특정 년월의 캘린더를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponseMessage.class))),
            @ApiResponse(responseCode = "400", description = "캘린더를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ApiResponseMessage.class)))
    })
    @GetMapping("/{calendarId}/monthly")
    public ResponseEntity<MonthlyCalendarResponse> getMonthlyCalendar(
            @PathVariable Long calendarId,
            @RequestParam int year,
            @RequestParam int month) {
        MonthlyCalendarResponse monthlyCalendar =
                calendarService.getMonthlyCalendar(calendarId, year, month);
        return ResponseEntity.ok(monthlyCalendar);

    }

    @Operation(summary = "현재 월 캘린더 조회", description = "현재 년월의 캘린더를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponseMessage.class))),
            @ApiResponse(responseCode = "404", description = "캘린더를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ApiResponseMessage.class)))
    })

    @GetMapping("/{calendarId}/current")
    public ResponseEntity<MonthlyCalendarResponse> getCurrentMonthCalendar(
            @PathVariable Long calendarId) {

        MonthlyCalendarResponse response = calendarService.getCurrentMonthCalendar(calendarId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원별 캘린더 조회", description = "특정 회원의 캘린더 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponseMessage.class))),
            @ApiResponse(responseCode = "404", description = "회원 또는 캘린더를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ApiResponseMessage.class)))
    })
    @GetMapping("/member/{memberId}")
    public ResponseEntity<CalendarResponse> getCalendarByMemberId(
            @PathVariable UUID memberId) {
        CalendarResponse response = calendarService.getCalendarByMemberId(memberId);
        return ResponseEntity.ok(response);
    }

}
