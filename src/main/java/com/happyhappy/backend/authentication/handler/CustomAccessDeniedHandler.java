package com.happyhappy.backend.authentication.handler;

import static com.happyhappy.backend.authentication.exception.AuthException.AuthexceptionCode.PERMISSION_DENIED;

import com.happyhappy.backend.authentication.exception.AuthException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        throw new AuthException(PERMISSION_DENIED, "접근 권한이 없습니다.", HttpStatus.FORBIDDEN);

    }
}
