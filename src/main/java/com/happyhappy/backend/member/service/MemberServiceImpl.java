package com.happyhappy.backend.member.service;

import com.happyhappy.backend.authentication.provider.TokenProvider;
import com.happyhappy.backend.member.domain.Member;
import com.happyhappy.backend.member.dto.MemberDetails;
import com.happyhappy.backend.member.dto.MemberDto.LoginRequest;
import com.happyhappy.backend.member.dto.MemberDto.LoginResponse;
import com.happyhappy.backend.member.dto.MemberDto.MemberInfoResponse;
import com.happyhappy.backend.member.repository.MemberRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {


    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final AuthenticationManager authenticationManager;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        try {

            Authentication authRequest = new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), loginRequest.getPassword()
            );
            Authentication authentication = authenticationManager.authenticate(authRequest);

            String accessToken = tokenProvider.generateAccessToken(authentication);

            MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();

            Member member = memberRepository.findById(memberDetails.getMemberId())
                    .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다."));

            MemberInfoResponse memberInfo = MemberInfoResponse.fromEntity(member);
            return LoginResponse.fromEntity(accessToken, null, memberInfo);
        } catch (BadCredentialsException e) {
            throw new IllegalArgumentException("아이디 혹은 비밀번호가 일치하지 않습니다.");
        }
    }


}
