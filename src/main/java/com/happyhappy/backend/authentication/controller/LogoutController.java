package com.happyhappy.backend.authentication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Logout")
@RestController
public class LogoutController {

    @Operation(summary = "로그아웃", description = "로그아웃을 합니다.")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie del = new Cookie("refreshToken", null);
        del.setHttpOnly(true);
        del.setSecure(true); // HTTPS 사용
        del.setPath("/");
        del.setMaxAge(0);
        response.addCookie(del);

        return ResponseEntity.ok("로그아웃 완료");
    }
}




