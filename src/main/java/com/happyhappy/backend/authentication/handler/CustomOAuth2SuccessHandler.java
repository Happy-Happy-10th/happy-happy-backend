package com.happyhappy.backend.authentication.handler;

import com.happyhappy.backend.authentication.provider.TokenProvider;
import com.happyhappy.backend.member.domain.Member;
import com.happyhappy.backend.member.dto.MemberDetails;
import com.happyhappy.backend.member.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {

        try {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = oauth2Token.getPrincipal();

            // 카카오 | 구글 구분해서 추출
            String registrationId = oauth2Token.getAuthorizedClientRegistrationId();
            String email;

            if ("google".equals(registrationId)) {
                email = oauth2User.getAttribute("email");
            } else if ("kakao".equals(registrationId)) {
                Map<String, Object> kakaoAccount = oauth2User.getAttribute("kakao_account");
                email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
            } else {
                throw new IllegalArgumentException("지원하지 않는 OAuth 제공자입니다.");
            }

            Member member = memberRepository.findByUsername(email)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            Authentication auth = createAuthentication(member);

            String accessToken = tokenProvider.generateAccessToken(auth);
            // TODO : 도메인 변경
            // 쿠키 설정으로 변경
            Cookie tokenCookie = new Cookie("access_token", accessToken);
            tokenCookie.setHttpOnly(true);
            // 배포 시 수정
            tokenCookie.setSecure(false); // http 사용 시 false
            tokenCookie.setPath("/");
            tokenCookie.setDomain(".yottaeyo.site");
            tokenCookie.setMaxAge(3600); // 1시간

            response.addCookie(tokenCookie);
            response.sendRedirect("http://www.yottaeyo.site/oauth/callback");

        } catch (Exception e) {
            log.error("OAuth2 로그인 처리 중 오류 발생", e);

            Cookie errorCookie = new Cookie("oauthError", "login_failed");
            errorCookie.setPath("/");
            errorCookie.setMaxAge(60); // 1분만
            response.addCookie(errorCookie);
            response.sendRedirect("http://www.yottaeyo.site/oauth/callback");
        }
    }

    private Authentication createAuthentication(Member member) {
        MemberDetails memberDetails = new MemberDetails(
                member.getMemberId(), member.getUsername(), member.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        return new UsernamePasswordAuthenticationToken(memberDetails, null,
                memberDetails.getAuthorities());
    }
}
