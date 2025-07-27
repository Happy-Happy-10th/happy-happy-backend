package com.happyhappy.backend.member.service;

import com.happyhappy.backend.authentication.provider.TokenProvider;
import com.happyhappy.backend.member.domain.Member;
import com.happyhappy.backend.member.domain.MemberSocialLoginInfo;
import com.happyhappy.backend.member.dto.MemberDetails;
import com.happyhappy.backend.member.dto.MemberDto.LoginRequest;
import com.happyhappy.backend.member.dto.MemberDto.LoginResponse;
import com.happyhappy.backend.member.dto.MemberDto.MemberInfoResponse;
import com.happyhappy.backend.member.repository.MemberRepository;
import com.happyhappy.backend.member.repository.MemberSocialLoginInfoRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
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
    private final MemberSocialLoginInfoRepository memberSocialLoginInfoRepository;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        try {

            Member member = memberRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));

            log.info("회원 찾음 - memberId: {}", member.getMemberId());

            Optional<MemberSocialLoginInfo> socialLoginInfo = memberSocialLoginInfoRepository.findByMemberId(
                    member.getMemberId());

            log.info("소셜 로그인 정보 확인 - 존재 여부: {}", socialLoginInfo.isPresent());

            if (socialLoginInfo.isPresent()) {
                throw new IllegalArgumentException("소셜 로그인으로 가입된 계정입니다.");
            }

            log.info("일반 로그인 진행");

            Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), loginRequest.getPassword());

            Authentication authentication = authenticationManager.authenticate(
                    authenticationRequest);

            String accessToken = tokenProvider.generateAccessToken(authentication);

            MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
            Member foundMember = memberRepository.findById(memberDetails.getMemberId())
                    .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다."));

            MemberInfoResponse memberInfo = MemberInfoResponse.fromEntity(foundMember);
            return LoginResponse.fromEntity(accessToken, null, memberInfo);
        } catch (IllegalArgumentException e) {
            log.error("로그인 실패 : {}", e.getMessage());
            throw e;
        } catch (BadCredentialsException e) {
            throw new IllegalArgumentException("아이디 혹은 비밀번호가 일치하지 않습니다.");
        }
    }


}
