package com.happyhappy.backend.authentication.controller;

import com.happyhappy.backend.common.response.ApiResponseCode;
import com.happyhappy.backend.common.response.ApiResponseMessage;
import com.happyhappy.backend.member.dto.EmailDto.UsernameCodeRequest;
import com.happyhappy.backend.member.dto.EmailDto.UsernameRequest;
import com.happyhappy.backend.member.dto.EmailDto.UsernameResponse;
import com.happyhappy.backend.member.dto.MemberDto.CheckResponse;
import com.happyhappy.backend.member.dto.MemberDto.LoginRequest;
import com.happyhappy.backend.member.dto.MemberDto.LoginResponse;
import com.happyhappy.backend.member.dto.MemberDto.SignupRequest;
import com.happyhappy.backend.member.dto.MemberDto.SignupResponse;
import com.happyhappy.backend.member.dto.MemberDto.UseridCheckRequest;
import com.happyhappy.backend.member.service.EmailService;
import com.happyhappy.backend.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
    private final EmailService emailService;

    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponseMessage> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response
    ) {
        LoginResponse loginResponse = memberService.login(loginRequest);

        // AccessToken 쿠키 저장
        Cookie accessCookie = new Cookie("accessToken", loginResponse.getAccessToken());
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true); // HTTPS 사용
        accessCookie.setPath("/");
        accessCookie.setDomain("yottaeyo.site");
        accessCookie.setMaxAge(24 * 60 * 60); // 1일

        // RefreshToken 쿠키 저장
        Cookie refreshCookie = new Cookie("refreshToken", loginResponse.getRefreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true); // HTTPS 사용
        refreshCookie.setPath("/");
        accessCookie.setDomain("yottaeyo.site");
        refreshCookie.setMaxAge(24 * 60 * 60); // 1일

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(
                ApiResponseMessage.success(loginResponse.getMemberInfo(), "로그인 성공"));
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponseMessage> signup(
            @RequestBody @Valid SignupRequest signupRequest) {
        SignupResponse response = memberService.signup(signupRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseMessage.success(response, "회원가입 성공"));
    }


    // 아이디(userid) 중복확인
    @PostMapping("/check-userid")
    public ResponseEntity<ApiResponseMessage> checkUserid(
            @RequestBody @Valid UseridCheckRequest request) {
        boolean isDuplicate = memberService.isUseridDuplicate(request.getUserid());
        CheckResponse response = CheckResponse.of(CheckResponse.CheckType.USERID, isDuplicate);

        return ResponseEntity.ok(
                ApiResponseMessage.success(response, isDuplicate ? "사용 중인 아이디" : "사용 가능")
        );
    }

    // 이메일(username) 중복확인
    @PostMapping("/check-username")
    public ResponseEntity<ApiResponseMessage> checkUsername(
            @RequestBody @Valid UsernameRequest request) {
        boolean isDuplicate = memberService.isUsernameDuplicate(request.getUsername());
        CheckResponse response = CheckResponse.of(CheckResponse.CheckType.USERNAME, isDuplicate);
        String message = isDuplicate ? "이미 사용중인 이메일 입니다." : "사용 가능한 이메일입니다.";
        return ResponseEntity.ok(
                ApiResponseMessage.success(response, message));
    }

    // 코드 전송
    @PostMapping("/send-code")
    public ResponseEntity<ApiResponseMessage> sendUsernameCode(
            @RequestBody @Valid UsernameRequest request) {
        emailService.sendCode(request.getUsername());
        return ResponseEntity.ok(ApiResponseMessage.success(null, "인증코드가 발송되었습니다."));
    }

    // 코드 인증
    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponseMessage> verifyUsernameCode(
            @RequestBody @Valid UsernameCodeRequest request) {
        boolean isValid = emailService.verifyCode(request.getUsername(), request.getCode());
        if (isValid) {
            return ResponseEntity.ok(
                    new ApiResponseMessage(ApiResponseCode.MEMBER_SUCCESS_000001,
                            UsernameResponse.of(true,
                                    ApiResponseCode.MEMBER_SUCCESS_000001.getMessageKo())));
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponseMessage.error(
                        HttpStatus.UNPROCESSABLE_ENTITY.value(),
                        "인증번호가 일치하지 않습니다.",
                        "MEMBER-ERR-000005"));
    }
}