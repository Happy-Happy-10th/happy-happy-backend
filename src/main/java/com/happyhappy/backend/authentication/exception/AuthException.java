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


    public static AuthException invalidRefreshToken() {
        return new AuthException(
                AuthexceptionCode.TOKEN_RENEW_FAILED,
                "유효하지 않은 Refresh Token입니다.",
                HttpStatus.UNAUTHORIZED
        );
    }
}
