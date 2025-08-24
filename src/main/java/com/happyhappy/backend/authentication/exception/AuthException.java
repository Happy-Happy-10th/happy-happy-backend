package com.happyhappy.backend.authentication.exception;

import com.happyhappy.backend.common.exception.CommonException;
import org.springframework.http.HttpStatus;

public class AuthException extends CommonException {

    public AuthException(AuthexceptionCode code, String message, HttpStatus httpStatus) {
        super(code.name(), message, httpStatus);
    }

    public enum AuthexceptionCode {
        UNAUTHENTICATED,
        CREDENTIAL_NOT_FOUND,
        TOKEN_ERROR,
        PERMISSION_DENIED,
        TOKEN_RENEW_FAILED,
        OAUTH_ERROR,
    }
}
