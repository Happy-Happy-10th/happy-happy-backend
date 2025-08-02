package com.happyhappy.backend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CommonException extends RuntimeException{
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    public CommonException(String code, String message, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

}
