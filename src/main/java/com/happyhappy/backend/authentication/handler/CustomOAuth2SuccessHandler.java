package com.happyhappy.backend.authentication.handler;

import com.happyhappy.backend.authentication.provider.TokenProvider;
import com.happyhappy.backend.member.domain.Member;
import com.happyhappy.backend.member.dto.MemberDetails;
import com.happyhappy.backend.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = oauth2Token.getPrincipal();
        String email = oauth2User.getAttribute("email");

        try {
            Member member = memberRepository.findByUsername(email)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            Authentication auth = createAuthentication(member);
            String accessToken = tokenProvider.generateAccessToken(auth);

            // 성공 시 프론트엔드로 토큰과 함께 리다이렉트 시킴
            // TODO : [FE와 논의 후 토큰 쿼리 파라메터로 전달] , 도메인 변경
            String redirectUrl = UriComponentsBuilder.fromUriString(
                            "http://localhost:3000/oauth/callback")
                    .queryParam("accessToken", accessToken)
                    .queryParam("success", "true")
                    .build().toUriString();

            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            log.error("OAuth2 로그인 처리 중 오류 발생", e);

            String errorRedirectUrl = UriComponentsBuilder.fromUriString(
                            "http://localhost:3000/oauth/callback")
                    .queryParam("error", "login_failed")
                    .queryParam("success", "false")
                    .build().toUriString();

            response.sendRedirect(errorRedirectUrl);
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
