package com.happyhappy.backend.member.service;

import com.happyhappy.backend.member.dto.MemberDto.LoginRequest;
import com.happyhappy.backend.member.dto.MemberDto.LoginResponse;
import com.happyhappy.backend.member.dto.MemberDto.SignupRequest;
import com.happyhappy.backend.member.dto.MemberDto.SignupResponse;

public interface MemberService {

    LoginResponse login(LoginRequest loginRequest);

    SignupResponse signup(SignupRequest signupRequest);

    boolean isUsernameDuplicate(String username);

    boolean isEmailDuplicate(String email);
}
