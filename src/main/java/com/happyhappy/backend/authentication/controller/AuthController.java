package com.happyhappy.backend.authentication.controller;

import com.happyhappy.backend.member.dto.MemberDto.LoginRequest;
import com.happyhappy.backend.member.dto.MemberDto.LoginResponse;
import com.happyhappy.backend.member.dto.MemberDto.SignupResponse;
import com.happyhappy.backend.member.dto.MemberDto.SignupRequest;
import com.happyhappy.backend.member.dto.MemberDto.CheckResponse;
import com.happyhappy.backend.member.dto.MemberDto.UsernameCheckRequest;
import com.happyhappy.backend.member.dto.EmailDto.EmailRequest;
import com.happyhappy.backend.member.dto.EmailDto.EmailCodeRequest;
import com.happyhappy.backend.member.dto.EmailDto.EmailResponse;
import com.happyhappy.backend.member.service.EmailService;
import com.happyhappy.backend.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {

    private final MemberService memberService;
    private final EmailService emailService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest,
                                               HttpServletResponse response) {

        LoginResponse loginResponse = memberService.login(loginRequest);

        Cookie accessCookie = new Cookie("accessToken", loginResponse.getAccessToken());
        accessCookie.setHttpOnly(true);
        // https 배포시 변경
        //accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(24 * 60 * 60); // 1일
        response.addCookie(accessCookie);


        Cookie refreshCookie = new Cookie("refreshToken", loginResponse.getRefreshToken());
        refreshCookie.setHttpOnly(true);
        // https 배포시 변경
        // refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(24 * 60 * 60); // 1일
        response.addCookie(refreshCookie);

        return ResponseEntity.ok().build();
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody @Valid SignupRequest signupRequest) {
        SignupResponse response = memberService.signup(signupRequest);

        return ResponseEntity.status(HttpStatus.CREATED) // 201 Created
                .body(response);
    }


    // 아이디 중복확인
    @GetMapping("/check-username")
    public ResponseEntity<CheckResponse> checkUsername(@RequestBody @Valid UsernameCheckRequest request) {
        boolean isDuplicate = memberService.isUsernameDuplicate(request.getUsername());
        return ResponseEntity.ok(
                CheckResponse.of(CheckResponse.CheckType.USERNAME, isDuplicate)
        );
    }

    // 이메일 중복확인
    @PostMapping("/check-email")
    public ResponseEntity<CheckResponse> checkEmail(@RequestBody @Valid EmailRequest request) {
        boolean isDuplicate = memberService.isEmailDuplicate(request.getEmail());
        return ResponseEntity.ok(
                CheckResponse.of(CheckResponse.CheckType.EMAIL, isDuplicate)
        );
    }


    // 코드 전송
    @PostMapping("/send-code")
    public ResponseEntity<?> sendEmailCode(@RequestBody @Valid EmailRequest request) {
        emailService.sendCode(request.getEmail());
        return ResponseEntity.ok().build();
    }

    // 코드 인증
    @PostMapping("/verify-code")
    public ResponseEntity<EmailResponse> verifyEmailCode(@RequestBody @Valid EmailCodeRequest request) {
        boolean isValid = emailService.verifyCode(request.getEmail(), request.getCode());
        if (isValid) {
            return ResponseEntity.ok(EmailResponse.of(true, "인증 성공"));
        } else {
            return ResponseEntity.badRequest().body(EmailResponse.of(false, "인증 실패"));
        }
    }
}