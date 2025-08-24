package com.happyhappy.backend.authentication.controller;

import com.happyhappy.backend.common.response.ApiResponseMessage;
import com.happyhappy.backend.member.domain.Member;
import com.happyhappy.backend.member.dto.EmailDto.UsernameCodeRequest;
import com.happyhappy.backend.member.dto.EmailDto.UsernameRequest;
import com.happyhappy.backend.member.dto.EmailDto.UsernameResponse;
import com.happyhappy.backend.member.dto.FindDto.FindPasswordRequest;
import com.happyhappy.backend.member.dto.FindDto.FindUserIdRequest;
import com.happyhappy.backend.member.dto.FindDto.FindUserIdResponse;
import com.happyhappy.backend.member.dto.FindDto.ResetPasswordRequest;
import com.happyhappy.backend.member.dto.MemberDto.AuthCodeResponse;
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
import java.util.Optional;
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

    @Operation(summary = "회원가입", description = "이름, 아이디, 이메일, 비밀번호 , 비밀번호 확인, 개인정보 수집 동의 필수정보를 입력받아 신규회원 생성합니다.")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponseMessage> signup(
            @RequestBody @Valid SignupRequest signupRequest) {
        SignupResponse response = memberService.signup(signupRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseMessage.success(response, "회원가입 성공"));
    }


    @Operation(summary = "아이디 중복확인", description = "아이디 입력받아 아이디 중복여부 확인합니다.")
    @PostMapping("/check-userid")
    public ResponseEntity<ApiResponseMessage> checkUserid(
            @RequestBody @Valid UseridCheckRequest request) {
        boolean isDuplicate = memberService.isUseridDuplicate(request.getUserid());
        CheckResponse response = CheckResponse.of(CheckResponse.CheckType.USERID, isDuplicate);
        String message = isDuplicate ? "이미 사용중인 아이디 입니다." : "사용 가능한 아이디입니다.";

        return ResponseEntity.ok(
                ApiResponseMessage.success(response, message)
        );
    }

    @Operation(summary = "이메일 중복확인", description = "이메일 입력받아 이메일 중복여부 확인합니다.")
    @PostMapping("/check-username")
    public ResponseEntity<ApiResponseMessage> checkUsername(
            @RequestBody @Valid UsernameRequest request) {
        boolean isDuplicate = memberService.isUsernameDuplicate(request.getUsername());
        CheckResponse response = CheckResponse.of(CheckResponse.CheckType.USERNAME, isDuplicate);
        String message = isDuplicate ? "이미 사용중인 이메일 입니다." : "사용 가능한 이메일입니다.";

        return ResponseEntity.ok(
                ApiResponseMessage.success(response, message));
    }

    @Operation(summary = "회원가입 인증번호전송", description = "이메일 입력받아 인증번호 요청합니다.")
    @PostMapping("/send-code")
    public ResponseEntity<ApiResponseMessage> sendUsernameCode(
            @RequestBody @Valid UsernameRequest request) {
        emailService.sendCode(request.getUsername());

        AuthCodeResponse response = new AuthCodeResponse(true, "인증코드가 발송되었습니다.", 300);
        return ResponseEntity.ok(ApiResponseMessage.success(response, "인증코드가 발송되었습니다."));
    }


    @Operation(summary = "이메일 인증번호확인", description = "이메일, 인증번호 입력받아 확인합니다.")
    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponseMessage> verifyUsernameCode(
            @RequestBody @Valid UsernameCodeRequest request) {
        boolean isValid = emailService.verifyCode(request.getUsername(), request.getCode());

        if (isValid) {
            UsernameResponse response = UsernameResponse.of(true, "인증번호가 확인되었습니다.");
            return ResponseEntity.ok(ApiResponseMessage.success(response));
        } else {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponseMessage.error(400, "인증번호가 일치하지 않습니다.", "EMAIL_CODE_INVALID"));
        }
    }

    @Operation(summary = "아이디찾기 인증번호전송", description = "이름, 이메일 입력받아 인증번호 요청합니다.")
    @PostMapping("/find-userid/send-code")
    public ResponseEntity<ApiResponseMessage> sendFindIdAuthCode(
            @RequestBody @Valid FindUserIdRequest request) {

        Optional<Member> member = memberService.findMember(request.getNickname(),
                request.getUsername());
        if (member.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(ApiResponseMessage.error(404, "일치하는 회원 정보가 없습니다.", "MEMBER_NOT_FOUND"));
        }
        emailService.sendCode(request.getUsername());

        AuthCodeResponse response = new AuthCodeResponse(true, "인증코드가 발송되었습니다.", 300);
        return ResponseEntity.ok(ApiResponseMessage.success(response, "인증코드가 발송되었습니다."));
    }


    @Operation(summary = "아이디 찾기", description = "이메일 인증 후 이름, 이메일 입력받아 아이디를 찾습니다.")
    @PostMapping("/find-userid")
    public ResponseEntity<ApiResponseMessage> findUserId(
            @RequestBody @Valid FindUserIdRequest request) {
        Optional<Member> member = memberService.findAfterEmailVerified(request.getNickname(),
                request.getUsername());

        FindUserIdResponse response = FindUserIdResponse.of(true, "아이디 찾기 성공",
                member.get().getUserId());

        return ResponseEntity.ok(ApiResponseMessage.success(response, "아이디 찾기 성공"));
    }

    @Operation(summary = "비밀번호찾기 인증번호전송", description = "이름, 아이디, 이메일 입력받아 인증번호 요청합니다.")
    @PostMapping("/find-password/send-code")
    public ResponseEntity<ApiResponseMessage> sendFindPasswordAuthCode(
            @RequestBody @Valid FindPasswordRequest request) {

        Optional<Member> member = memberService.findMember(
                request.getNickname(), request.getUsername());

        if (member.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(ApiResponseMessage.error(404, "일치하는 회원 정보가 없습니다.", "MEMBER_NOT_FOUND"));
        }
        emailService.sendCode(request.getUsername());
        AuthCodeResponse response = new AuthCodeResponse(true, "인증코드가 발송되었습니다.", 300);
        return ResponseEntity.ok(ApiResponseMessage.success(response, "인증코드가 발송되었습니다."));
    }


    @Operation(summary = "비밀번호 재설정", description = "인증된 사용자만 새 비밀번호를 재설정합니다.")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponseMessage> resetPassword(
            @RequestBody @Valid ResetPasswordRequest request) {

        memberService.resetPassword(request.getUsername(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponseMessage.success(null, "비밀번호가 재설정되었습니다."));
    }

}