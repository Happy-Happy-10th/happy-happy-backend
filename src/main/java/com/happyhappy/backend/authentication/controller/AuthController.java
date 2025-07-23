package com.happyhappy.backend.authentication.controller;

import com.happyhappy.backend.common.response.ApiResponseCode;
import com.happyhappy.backend.common.response.ApiResponseMessage;
import com.happyhappy.backend.member.dto.MemberDto.LoginRequest;
import com.happyhappy.backend.member.dto.MemberDto.LoginResponse;
import com.happyhappy.backend.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth")
@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {

    private final MemberService memberService;

    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponseMessage> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = memberService.login(loginRequest);

        ApiResponseMessage message = new ApiResponseMessage(ApiResponseCode.COMMON_SUCCESS_000001,loginResponse);

        return new ResponseEntity<ApiResponseMessage>(message, HttpStatus.OK);

    }
}
