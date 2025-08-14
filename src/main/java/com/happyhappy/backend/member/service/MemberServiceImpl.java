package com.happyhappy.backend.member.service;

import static com.happyhappy.backend.common.util.LogMasking.maskEmail;
import static com.happyhappy.backend.common.util.LogMasking.maskUserId;

import com.happyhappy.backend.authentication.provider.TokenProvider;
import com.happyhappy.backend.common.util.InputNormalizer;
import com.happyhappy.backend.member.domain.Member;
import com.happyhappy.backend.member.domain.MemberSocialLoginInfo;
import com.happyhappy.backend.member.dto.MemberDetails;
import com.happyhappy.backend.member.dto.MemberDto.LoginRequest;
import com.happyhappy.backend.member.dto.MemberDto.LoginResponse;
import com.happyhappy.backend.member.dto.MemberDto.MemberInfoResponse;
import com.happyhappy.backend.member.dto.MemberDto.SignupRequest;
import com.happyhappy.backend.member.dto.MemberDto.SignupResponse;
import com.happyhappy.backend.member.repository.MemberRepository;
import com.happyhappy.backend.member.repository.MemberSocialLoginInfoRepository;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {


    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final AuthenticationManager authenticationManager;
    private final MemberSocialLoginInfoRepository memberSocialLoginInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            Member member = memberRepository.findByUserId(loginRequest.getUserid())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));

            log.info("회원 찾음 - memberId: {}", member.getMemberId());

            Optional<MemberSocialLoginInfo> socialLoginInfo =
                    memberSocialLoginInfoRepository.findByMemberId(member.getMemberId());

            log.info("소셜 로그인 정보 확인 - 존재 여부: {}", socialLoginInfo.isPresent());

            if (socialLoginInfo.isPresent()) {
                throw new IllegalArgumentException("소셜 로그인으로 가입된 계정입니다.");
            }

            log.info("일반 로그인 진행");

            Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(
                    member.getUsername(), loginRequest.getPassword());
            Authentication authentication = authenticationManager.authenticate(
                    authenticationRequest);

            String accessToken = tokenProvider.generateAccessToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(authentication);

            MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
            Member foundMember = memberRepository.findById(memberDetails.getMemberId())
                    .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다."));

            MemberInfoResponse memberInfo = MemberInfoResponse.fromEntity(foundMember);
            return LoginResponse.fromEntity(accessToken, refreshToken, memberInfo);

        } catch (IllegalArgumentException e) {
            log.error("로그인 실패 : {}", e.getMessage());
            throw e;
        } catch (BadCredentialsException e) {
            throw new IllegalArgumentException("아이디 혹은 비밀번호가 일치하지 않습니다.");
        }
    }

    @Override
    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {

        String username = InputNormalizer.username(signupRequest.getUsername());
        String userid = InputNormalizer.userId(signupRequest.getUserid());
        String nickname = InputNormalizer.nickname(signupRequest.getNickname());
        nickname = nickname.isEmpty() ? null : nickname;

        if (username.isEmpty() || userid.isEmpty()) {
            log.warn("회원가입 - userid 또는 username 입력값 없음 :  userId={}, username={}",
                    maskUserId(userid), maskEmail(username));
            throw new IllegalArgumentException("아이디 또는 이메일 값이 비어 있습니다.");
        }

        if (memberRepository.existsByUserId(userid)) {
            log.warn("회원가입 - userid 중복 : {}", maskUserId(userid));
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        if (memberRepository.existsByUsername(username)) {
            log.warn("회원가입 - username 중복 : {}", maskEmail(username));
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        if (!emailService.isUsernameVerified(username)) {
            log.warn("회원가입 - username 인증안됨 : {}", maskEmail(username));
            throw new IllegalArgumentException("이메일 인증을 먼저 완료해주세요.");
        }

        if (!signupRequest.isPasswordConfirmed()) {
            log.warn("회원가입 - userId={} password 일치하지않음 ", maskUserId(userid));
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        Member newMember = Member.builder()
                .username(username)
                .nickname(nickname)
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .userId(userid)
                .isActive(true)
                .marketingAgreedAt(LocalDateTime.now())
                .build();

        newMember.createCalendar();

        try {
            Member saved = memberRepository.save(newMember);
            log.info("회원가입 완료 memberId={} userId={} ",
                    saved.getMemberId(), maskUserId(userid));
            emailService.consumeVerification(username);
            return SignupResponse.fromEntity(saved);

        } catch (DataIntegrityViolationException e) {

            throw new IllegalArgumentException("이미 존재하는 아이디 또는 이메일입니다.");
        }
    }

    // 아이디 중복
    @Override
    public boolean isUseridDuplicate(String userid) {
        String v = InputNormalizer.userId(userid);
        if (v.isEmpty()) {
            log.warn("userId 중복확인 - userId 입력값 없음");
            throw new IllegalArgumentException("아이디는 필수 입력값입니다.");
        }
        return memberRepository.existsByUserId(v);
    }

    // 이메일 중복
    @Override
    public boolean isUsernameDuplicate(String username) {
        String v = InputNormalizer.username(username);
        if (v.isEmpty()) {
            log.warn("username 중복확인 - username 입력값 없음");
            throw new IllegalArgumentException("이메일은 필수 입력값입니다.");
        }
        return memberRepository.existsByUsername(v);
    }


}
