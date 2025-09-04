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
                && StringUtils.hasText(refreshToken)) {

            if (tokenProvider.validateToken(refreshToken)) {
                Authentication auth = tokenProvider.getAuthentication(refreshToken);
                String newAccessToken = tokenProvider.generateAccessToken(auth);
                response.setHeader("Authorization", "Bearer" + newAccessToken);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                // 만료된 토큰들을 모두 삭제
                Cookie delRefresh = new Cookie("refreshToken", null);
                delRefresh.setHttpOnly(true);
                delRefresh.setSecure(true); // HTTPS 사용
                delRefresh.setPath("/");
                delRefresh.setMaxAge(0);

                Cookie delAccess = new Cookie("accessToken", null);
                delAccess.setHttpOnly(true);
                delAccess.setSecure(true); // HTTPS 사용
                delAccess.setPath("/");
                delAccess.setMaxAge(0);

                response.addCookie(delRefresh);
                response.addCookie(delAccess);
                response.sendRedirect("https://api.yottaeyo.site/api/login");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }


    private String resolveToken(HttpServletRequest request) {
        // 먼저 Authorization 헤더에서 토큰 확인 (API 호출용)
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // Authorization 헤더가 없으면 쿠키에서 AccessToken 확인 (웹 브라우저용)
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("accessToken".equals(c.getName())) {
                    return c.getValue();
                }
            }
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
