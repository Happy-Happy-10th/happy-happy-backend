package com.happyhappy.backend.common.exception;

import com.happyhappy.backend.authentication.exception.AuthException;
import com.happyhappy.backend.calendar.exception.CalendarException.CalendarAccessDeniedException;
import com.happyhappy.backend.calendar.exception.CalendarException.CalendarNotFoundException;
import com.happyhappy.backend.calendar.exception.CalendarException.InvalidCalendarSettingException;
import com.happyhappy.backend.calendar.exception.EventException.RepeatPeriodException;
import com.happyhappy.backend.common.response.ApiResponseMessage;
import java.nio.file.AccessDeniedException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = AuthException.class)
    public ResponseEntity<ApiResponseMessage> handleAuthException(AuthException e) {
        log.info("[인증 과정에 오류 발생] {}", e.getMessage());
        ApiResponseMessage response = ApiResponseMessage.error(
                e.getHttpStatus().value(),
                e.getMessage(),
                e.getCode()
        );
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseMessage> handleAccessDeniedException(AccessDeniedException e) {
        log.info("권한 오류 발생 {}", e.getMessage());
        ApiResponseMessage response = ApiResponseMessage.error(
                403,
                "접근 권한이 없습니다.",
                "ACCESS_DENIED"
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseMessage> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {

        String message = e.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.info("입력값이 올바르지 않음 : {}", message);
        ApiResponseMessage response = ApiResponseMessage.error(
                400,
                message,
                "VALIDATION_ERROR"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(CalendarNotFoundException.class)
    public ResponseEntity<ApiResponseMessage> handleCalendarNotFound(CalendarNotFoundException e) {
        ApiResponseMessage response = ApiResponseMessage.error(
                404,
                "캘린더를 찾을 수 없습니다.",
                "CALENDAR_NOT_FOUND"
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(CalendarAccessDeniedException.class)
    public ResponseEntity<ApiResponseMessage> handleCalendarAccessDenied(
            CalendarAccessDeniedException e) {
        ApiResponseMessage response = ApiResponseMessage.error(
                403, "캘린더 접근 권한이 없습니다.", "CALENDAR_ACCESS_DENIED"
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(InvalidCalendarSettingException.class)
    public ResponseEntity<ApiResponseMessage> handleInvalidCalendarSetting(
            InvalidCalendarSettingException e) {
        ApiResponseMessage response = ApiResponseMessage.error(
                400, e.getMessage(), "INVALID_CALENDAR_SETTING"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseMessage> handleIllegalArgument(IllegalArgumentException e) {
        ApiResponseMessage message = ApiResponseMessage.error(
                400,
                e.getMessage(),
                "BAD_REQUEST"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseMessage> handlerGeneral(Exception e) {
        log.error("예상치 못한 오류 : ", e);
        ApiResponseMessage response = ApiResponseMessage.error(
                500,
                "내부 서버 오류가 발생했습니다.",
                "INTERNAL_SERVER_ERROR"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(RepeatPeriodException.class)
    public ResponseEntity<ApiResponseMessage> handleRepeatPeriodException(RepeatPeriodException e) {
        ApiResponseMessage response = ApiResponseMessage.error(
                400,
                e.getMessage(),
                "INVALID_REPEAT_PERIOD"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}

