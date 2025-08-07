package com.happyhappy.backend.authentication.handler;

import com.happyhappy.backend.calendar.exception.CalendarException.CalendarAccessDeniedException;
import com.happyhappy.backend.calendar.exception.CalendarException.CalendarNotFoundException;
import com.happyhappy.backend.calendar.exception.CalendarException.InvalidCalendarSettingException;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomCalendarHandler {

    @ExceptionHandler(CalendarNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCalendarNotFound(
            CalendarNotFoundException e) {

        log.warn("Calendar not found : {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse("CALENDAR_NOT_FOUND", "캘린더를 찾을 수 없습니다."));
    }

    @ExceptionHandler(CalendarAccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleCalendarAccessDenied(
            CalendarAccessDeniedException e) {

        log.warn("Calendar access denied : {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(createErrorResponse("CALENDAR_ACCESS_DENIED", "캘린더 접근 권한이 없습니다."));
    }

    @ExceptionHandler(InvalidCalendarSettingException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCalendarSetting(
            InvalidCalendarSettingException e) {

        log.warn("Invalid calendar setting : {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("INVALID_CALENDAR_SETTING", e.getMessage()));
    }

    private Map<String, Object> createErrorResponse(String code, String message) {
        return Map.of("code", code, "message", message, "timestamp", LocalDateTime.now());
    }
}
