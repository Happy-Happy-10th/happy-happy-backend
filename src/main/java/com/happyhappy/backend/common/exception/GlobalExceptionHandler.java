package com.happyhappy.backend.common.exception;

import com.happyhappy.backend.authentication.exception.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = AuthException.class)
    public ResponseEntity<ErrorInfo> handleException(AuthException e) {
        log.info("[인증 과정에 오류 발생] {}", e.getMessage());
        return responseException(e.getCode(), e.getMessage(), e.getHttpStatus());
    }

    private ResponseEntity<ErrorInfo> responseException(String code, String message,
            HttpStatus httpStatus) {
        ErrorInfo errorInfo = new ErrorInfo(code, message);
        return ResponseEntity.status(httpStatus).body(errorInfo);
    }

    private record ErrorInfo(String code, String message) {

    }
}
