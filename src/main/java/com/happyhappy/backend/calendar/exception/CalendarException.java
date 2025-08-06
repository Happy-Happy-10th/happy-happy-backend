package com.happyhappy.backend.calendar.exception;

import com.happyhappy.backend.common.exception.CommonException;
import java.util.UUID;
import org.springframework.http.HttpStatus;

public class CalendarException extends CommonException {

    public CalendarException(String code, String message,
            HttpStatus httpStatus) {
        super(code, message, httpStatus);
    }

    public static class CalendarNotFoundException extends RuntimeException {

        public CalendarNotFoundException(String message) {
            super(message);
        }

        public CalendarNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }

        public CalendarNotFoundException(Long calendarId) {
            super("캘린더를 찾을 수 없습니다. ID: " + calendarId);
        }
    }

    public static class CalendarAccessDeniedException extends RuntimeException {

        public CalendarAccessDeniedException(String message) {
            super(message);
        }

        public CalendarAccessDeniedException(Long calendarId, UUID memberId) {
            super("캘린더 접근 권한이 없습니다. calendarId: " + calendarId + ", memberId: " + memberId);
        }
    }

    public static class InvalidCalendarSettingException extends RuntimeException {

        public InvalidCalendarSettingException(String message) {
            super(message);
        }
    }

}
