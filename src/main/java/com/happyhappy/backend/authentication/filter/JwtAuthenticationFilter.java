package com.happyhappy.backend.authentication.filter;

import com.happyhappy.backend.authentication.provider.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class
JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);
        String refreshToken = resolveRefreshToken(request);

        if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // access token 만료 + refresh token
        else if (StringUtils.hasText(token) && tokenProvider.isTokenExpired(token)
                && StringUtils.hasText(refreshToken)){

            if (tokenProvider.validateToken(refreshToken)) {
                Authentication auth = tokenProvider.getAuthentication(refreshToken);
                String newAccessToken = tokenProvider.generateAccessToken(auth);
                response.setHeader("Authorization", "Bearer " + newAccessToken);
                SecurityContextHolder.getContext().setAuthentication(auth);
                }

            else {
                Cookie del = new Cookie("refreshToken", null);
                del.setHttpOnly(true);
                // https일때만 쿠키 전송
                // del.setSecure(true);
                del.setPath("/");
                del.setMaxAge(0);
                response.addCookie(del);
                response.sendRedirect("/login");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }


    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }


    private String resolveRefreshToken(HttpServletRequest req) {
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                if ("refreshToken".equals(c.getName())) {
                    return c.getValue();
                }
            }
        }
        return null;
    }

}
