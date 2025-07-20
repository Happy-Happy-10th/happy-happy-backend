package com.happyhappy.backend.member.service;

import com.happyhappy.backend.member.dto.MemberDto.LoginRequest;
import com.happyhappy.backend.member.dto.MemberDto.LoginResponse;

public interface MemberService {

    LoginResponse login(LoginRequest loginRequest);

}
