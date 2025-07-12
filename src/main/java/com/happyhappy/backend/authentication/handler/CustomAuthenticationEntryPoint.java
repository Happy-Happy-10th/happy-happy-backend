package com.happyhappy.backend.authentication.handler;

import static com.happyhappy.backend.authentication.exception.AuthException.AuthexceptionCode.UNAUTHENTICATED;

import com.happyhappy.backend.authentication.exception.AuthException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.servlet.HandlerExceptionResolver;

@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {


    private String activeProfile;

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        if ("test".equals(activeProfile)) {
            // HandlerExceptionResolver 사용 안 하고 직접 응답
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Unauthorized\"}");
        } else {
            AuthException authException = new AuthException(UNAUTHENTICATED, "로그인이 필요합니다.",
                    HttpStatus.UNAUTHORIZED);
            handlerExceptionResolver.resolveException(request, response, null, authException);
        }


    }
}
