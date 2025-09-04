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
            String refreshToken = tokenProvider.generateRefreshToken(auth);

            // AccessToken 쿠키 저장
            Cookie accessCookie = new Cookie("accessToken", accessToken);
            accessCookie.setHttpOnly(true);
            accessCookie.setSecure(true); // HTTPS 사용
            accessCookie.setPath("/");
            accessCookie.setDomain("yottaeyo.site");
            accessCookie.setMaxAge(24 * 60 * 60); // 1일

            // RefreshToken 쿠키 저장
            Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true); // HTTPS 사용
            refreshCookie.setPath("/");
            refreshCookie.setDomain("yottaeyo.site");
            refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일

            response.addCookie(accessCookie);
            response.addCookie(refreshCookie);

            // URL에는 성공 상태만 표시
            response.sendRedirect("https://api.yottaeyo.site/oauth/callback?success=true");

        } catch (Exception e) {
            log.error("OAuth2 로그인 처리 중 오류 발생", e);

            String errorMessage = e.getMessage();
            String redirectUrl = String.format(
                    "https://api.yottaeyo.site/oauth/callback?success=false&error=%s",
                    java.net.URLEncoder.encode(errorMessage != null ? errorMessage : "oauth_failed",
                            "UTF-8"));

            response.sendRedirect(redirectUrl);
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
