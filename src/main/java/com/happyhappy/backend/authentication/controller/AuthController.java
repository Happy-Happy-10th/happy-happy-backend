package com.happyhappy.backend.authentication.controller;

import com.happyhappy.backend.common.response.ApiResponseCode;
import com.happyhappy.backend.common.response.ApiResponseMessage;
import com.happyhappy.backend.member.dto.MemberDto.LoginRequest;
import com.happyhappy.backend.member.dto.MemberDto.LoginResponse;
import com.happyhappy.backend.member.dto.MemberDto.SignupResponse;
import com.happyhappy.backend.member.dto.MemberDto.SignupRequest;
import com.happyhappy.backend.member.dto.MemberDto.CheckResponse;
import com.happyhappy.backend.member.dto.MemberDto.UseridCheckRequest;
import com.happyhappy.backend.member.dto.EmailDto.UsernameRequest;
import com.happyhappy.backend.member.dto.EmailDto.UsernameCodeRequest;
import com.happyhappy.backend.member.dto.EmailDto.UsernameResponse;
import com.happyhappy.backend.member.service.EmailService;
import com.happyhappy.backend.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Auth")
@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {

    private final MemberService memberService;
    private final EmailService emailService;

    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponseMessage> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response
    ) {
        LoginResponse loginResponse = memberService.login(loginRequest);

        // refreshToken 쿠키 저장
        Cookie refreshCookie = new Cookie("refreshToken", loginResponse.getRefreshToken());
        refreshCookie.setHttpOnly(true);
        //refreshCookie.setSecure(true); // HTTPS 배포 시 활성화
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(24 * 60 * 60); // 1일
        response.addCookie(refreshCookie);

        ApiResponseMessage message = new ApiResponseMessage(ApiResponseCode.COMMON_SUCCESS_000001, loginResponse);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody @Valid SignupRequest signupRequest) {
        SignupResponse response = memberService.signup(signupRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }


    // 아이디(userid) 중복확인
    @PostMapping("/check-userid")
    public ResponseEntity<CheckResponse> checkUserid(@RequestBody @Valid UseridCheckRequest request) {
        boolean isDuplicate = memberService.isUseridDuplicate(request.getUserid());
        return ResponseEntity.ok(
                CheckResponse.of(CheckResponse.CheckType.USERID, isDuplicate)
        );
    }

    // 이메일(username) 중복확인
    @PostMapping("/check-username")
    public ResponseEntity<CheckResponse> checkUsername(@RequestBody @Valid UsernameRequest request) {
        boolean isDuplicate = memberService.isUsernameDuplicate(request.getUsername());
        return ResponseEntity.ok(
                CheckResponse.of(CheckResponse.CheckType.USERNAME, isDuplicate)
        );
    }


    // 코드 전송
    @PostMapping("/send-code")
    public ResponseEntity<?> sendUsernameCode(@RequestBody @Valid UsernameRequest request) {
        emailService.sendCode(request.getUsername());
        return ResponseEntity.ok().build();
    }

    // 코드 인증
    @PostMapping("/verify-code")
    public ResponseEntity<UsernameResponse> verifyUsernameCode(@RequestBody @Valid UsernameCodeRequest request) {
        boolean isValid = emailService.verifyCode(request.getUsername(), request.getCode());
        if (isValid) {
            return ResponseEntity.ok(UsernameResponse.of(true, "인증 성공"));
        } else {
            return ResponseEntity.badRequest().body(UsernameResponse.of(false, "인증 실패"));
        }
    }
}