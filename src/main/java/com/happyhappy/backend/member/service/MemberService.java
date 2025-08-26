package com.happyhappy.backend.member.service;

import com.happyhappy.backend.member.dto.MemberDto.LoginRequest;
import com.happyhappy.backend.member.dto.MemberDto.LoginResponse;
import com.happyhappy.backend.member.dto.MemberDto.MemberInfoResponse;
import com.happyhappy.backend.member.dto.MemberDto.SignupRequest;
import com.happyhappy.backend.member.dto.MemberDto.SignupResponse;

public interface MemberService {

    LoginResponse login(LoginRequest loginRequest);

    SignupResponse signup(SignupRequest signupRequest);

    boolean isUseridDuplicate(String userid);

    boolean isUsernameDuplicate(String username);

    MemberInfoResponse getMemberInfoByToken(String token);
}
