package com.happyhappy.backend.common.exception;

import com.happyhappy.backend.authentication.exception.AuthException;
import com.happyhappy.backend.calendar.exception.CalendarException.CalendarNotFoundException;
import com.happyhappy.backend.common.response.ApiResponseCode;
import com.happyhappy.backend.common.response.ApiResponseMessage;
import java.nio.file.AccessDeniedException;
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
    public ResponseEntity<ErrorInfo> handleAuthException(AuthException e) {
        log.info("[인증 과정에 오류 발생] {}", e.getMessage());
        return responseException(e.getCode(), e.getMessage(), e.getHttpStatus());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorInfo> handleAccessDeniedException(AccessDeniedException e) {
        log.info("권한 오류 발생 {}", e.getMessage());
        return responseException("PERMIMSSION_DENIED", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorInfo> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().stream().findFirst().map(
                DefaultMessageSourceResolvable::getDefaultMessage).orElse("입력값이 올바르지 않습니다.");
        log.info("입력값이 올바르지 않음 : {}", e.getMessage());

        return responseException("VALIDATION_FAILED", message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CalendarNotFoundException.class)
    public ResponseEntity<ApiResponseMessage> handleCalendarNotFound(CalendarNotFoundException e) {
        return ResponseEntity.ok(
                new ApiResponseMessage(ApiResponseCode.COMMON_ERROR_000003, null)
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseMessage> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.ok(
                new ApiResponseMessage(ApiResponseCode.BAD_REQUEST, e.getMessage())
        );
    }

    private ResponseEntity<ErrorInfo> responseException(String code, String message,
            HttpStatus httpStatus) {
        ErrorInfo errorInfo = new ErrorInfo(code, message);
        return ResponseEntity.status(httpStatus).body(errorInfo);
    }

    private record ErrorInfo(String code, String message) {

    }
}
