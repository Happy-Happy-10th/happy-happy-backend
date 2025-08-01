package com.happyhappy.backend.authentication.controller;

import com.happyhappy.backend.authentication.provider.TokenProvider;
import com.happyhappy.backend.member.dto.MemberDto.LoginRequest;
import com.happyhappy.backend.member.dto.MemberDto.LoginResponse;
import com.happyhappy.backend.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.remote.JMXAuthenticator;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {

    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest ,
                                               HttpServletResponse response) {


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(24 * 60 * 60);
        response.addCookie(refreshCookie);


        return ResponseEntity.ok(memberService.login(loginRequest));
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest request) {
        SignupResponse response = memberService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED) // 201 Created
                .body(response);
    }


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

