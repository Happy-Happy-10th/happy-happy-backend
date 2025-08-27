package com.happyhappy.backend.calendar.controller;

import com.happyhappy.backend.calendar.domain.CalendarDto.AiSearchRegionRequest;
import com.happyhappy.backend.calendar.domain.CalendarDto.SettingsResponse;
import com.happyhappy.backend.calendar.domain.CalendarDto.TimeFormatRequest;
import com.happyhappy.backend.calendar.domain.CalendarDto.WeekStartDayRequest;
import com.happyhappy.backend.calendar.service.CalendarSettingService;
import com.happyhappy.backend.common.response.ApiResponseMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar/{calendarId}/settings")
@Tag(name = "Calendar Settings", description = "캘린더 테그 관련 api")
public class CalendarSettingsController {

    private final CalendarSettingService calendarSettingService;

    @Operation(summary = "캘린더 설정 조회", description = "캘린더의 전체 환경설정을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponseMessage.class))),
            @ApiResponse(responseCode = "404", description = "캘린더를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ApiResponseMessage.class)))
    })
    @GetMapping
    public ResponseEntity<ApiResponseMessage> getSettings(
            @PathVariable Long calendarId) {

        SettingsResponse response = calendarSettingService.getSettings(calendarId);
        return ResponseEntity.ok(ApiResponseMessage.success(response));
    }

    @Operation(summary = "주 시작일 변경", description = "캘린더의 주 시작일을 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "캘린더를 찾을 수 없음")
    })
    @PatchMapping("/week-start-day")
    public ResponseEntity<ApiResponseMessage> updateWeekStartDay(
            @PathVariable Long calendarId,
            @RequestBody WeekStartDayRequest request) {
        calendarSettingService.updateWeekStartDay(calendarId, request.getWeekStartDay());
        return ResponseEntity.ok(ApiResponseMessage.success(null, "주 시작일이 변경되었습니다."));
    }

    @Operation(summary = "시간 형식 변경", description = "12시간/24시간 형식을 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "캘린더를 찾을 수 없음")
    })
    @PatchMapping("/time-format")
    public ResponseEntity<ApiResponseMessage> updateTimeFormat(
            @PathVariable Long calendarId,
            @RequestBody TimeFormatRequest request) {
        calendarSettingService.updateTimeFormat(calendarId, request.getTimeFormat());
        return ResponseEntity.ok(ApiResponseMessage.success(null, "시간 형식이 변경되었습니다."));
    }

    @Operation(summary = "AI 검색 지역 설정",
            description = "AI 검색 시 사용할 지역을 설정합니다. sigunguCode가 null이면 시도 전체를 의미합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "캘린더 또는 지역을 찾을 수 없음")
    })
    @PatchMapping("/ai-search-region")
    public ResponseEntity<ApiResponseMessage> updateAiSearchRegion(
            @PathVariable Long calendarId,
            @RequestBody AiSearchRegionRequest request) {
        calendarSettingService.updateAiSearchRegion(
                calendarId,
                request.getSidoCode(),
                request.getSigunguCode()
        );
        return ResponseEntity.ok(ApiResponseMessage.success(null, "ai 검색 지역이 변경되었습니다."));
    }


}
