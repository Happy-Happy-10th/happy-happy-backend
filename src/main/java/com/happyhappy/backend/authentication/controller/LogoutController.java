package com.happyhappy.backend.authentication.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogoutController {
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie del = new Cookie("refreshToken", null);
        del.setHttpOnly(true);
        // HTTPS 배포 시 변경
        // del.setSecure(true);
        del.setPath("/");
        del.setMaxAge(0);
        response.addCookie(del);

        return ResponseEntity.ok("로그아웃 완료");
    }
}




