package com.happyhappy.backend.member.service;

import com.happyhappy.backend.member.domain.Member;
import com.happyhappy.backend.member.dto.MemberDto.LoginRequest;
import com.happyhappy.backend.member.dto.MemberDto.LoginResponse;
import com.happyhappy.backend.member.dto.MemberDto.SignupRequest;
import com.happyhappy.backend.member.dto.MemberDto.SignupResponse;
import java.util.Optional;

public interface MemberService {

    LoginResponse login(LoginRequest loginRequest);

    SignupResponse signup(SignupRequest signupRequest);

    boolean isUseridDuplicate(String userid);

    boolean isUsernameDuplicate(String username);

    Optional<Member> findMember(String nickname, String username);

    void resetPassword(String username, String newPassword);

    Optional<Member> findAfterEmailVerified(String nickname, String username);
}
