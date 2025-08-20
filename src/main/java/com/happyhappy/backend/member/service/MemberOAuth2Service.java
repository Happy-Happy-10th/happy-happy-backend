package com.happyhappy.backend.member.service;


import com.happyhappy.backend.member.domain.Member;
import com.happyhappy.backend.member.domain.MemberSocialLoginInfo;
import com.happyhappy.backend.member.enums.SocialLoginProviderType;
import com.happyhappy.backend.member.repository.MemberRepository;
import com.happyhappy.backend.member.repository.MemberSocialLoginInfoRepository;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberOAuth2Service extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final MemberSocialLoginInfoRepository memberSocialLoginInfoRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialLoginProviderType providerType = SocialLoginProviderType.fromRegistrationId(
                registrationId);

        String email = extractEmail(oauth2User, providerType);
        String name = extractName(oauth2User, providerType);
        String providerId = extractProviderId(oauth2User, providerType);

        Member member = findOrCreateOAuthMember(email, name, providerId, providerType);

        log.info("Oauth2 로그인 성공 - memberId : {}", member.getMemberId());

        return oauth2User;
    }

    private Member findOrCreateOAuthMember(String email, String name, String providerId,
            SocialLoginProviderType providerType) {
        Optional<MemberSocialLoginInfo> socialLoginInfo = memberSocialLoginInfoRepository.findByProviderTypeAndProviderId(
                providerType, providerId);

        if (socialLoginInfo.isPresent()) {
            Optional<Member> member = memberRepository.findById(
                    socialLoginInfo.get().getMemberId());
            if (member.isPresent()) {
                log.info("기존 소설 로그인 회원 {}", email);
                return member.get();
            }
        }

        Optional<Member> existingMember = memberRepository.findByUsername(email);
        if (existingMember.isPresent()) {
            Member member = existingMember.get();

            boolean hasSocialLogin = memberSocialLoginInfoRepository.findByMemberId(
                    member.getMemberId()).isPresent();
            if (!hasSocialLogin) {
                OAuth2Error oauth2Error = new OAuth2Error("already_registered", 
                    "일반 회원으로 가입된 이메일입니다. 일반 로그인을 이용해주세요", null);
                throw new OAuth2AuthenticationException(oauth2Error);
            }

            log.info("기존 소셜 회원 : {}", email);
            return member;
        }

        log.info("신규 소셜 회원 생성 : {}", email);
        Member newMember = createNewOAuthMember(email, name);
        createSocialLoginInfo(newMember.getMemberId(), providerId, providerType);

        return newMember;
    }

    private Member createNewOAuthMember(String email, String name) {
        Member newMember = Member.builder()
                .username(email)
                .nickname(name != null ? name : "OAuth 사용자")
                .password("OAUTH_USER")
                .imageUrl(null)
                .isActive(true)
                .build();

        newMember.createCalendar();
        
        return memberRepository.save(newMember);
    }

    private void createSocialLoginInfo(UUID memberId, String providerId,
            SocialLoginProviderType providerType) {
        MemberSocialLoginInfo socialLoginInfo = MemberSocialLoginInfo.builder()
                .memberId(memberId)
                .providerId(providerId)
                .providerType(providerType)
                .build();
        memberSocialLoginInfoRepository.save(socialLoginInfo);
    }

    private String extractEmail(OAuth2User oauth2User, SocialLoginProviderType providerType) {
        return switch (providerType) {
            case GOOGLE -> oauth2User.getAttribute("email");
            case KAKAO -> {
                Map<String, Object> kakaoAccount = oauth2User.getAttribute("kakao_account");
                if (kakaoAccount != null) {
                    yield (String) kakaoAccount.get("email");
                }
                yield null;
            }
        };
    }

    private String extractName(OAuth2User oauth2User, SocialLoginProviderType providerType) {
        return switch (providerType) {
            case GOOGLE -> oauth2User.getAttribute("name");
            case KAKAO -> {
                Map<String, Object> properties = oauth2User.getAttribute("properties");
                if (properties != null) {
                    yield (String) properties.get("nickname");
                }
                yield null;
            }
        };
    }


    private String extractProviderId(OAuth2User oauth2User, SocialLoginProviderType providerType) {

        String providerId = switch (providerType) {
            case GOOGLE -> oauth2User.getAttribute("id");
            case KAKAO -> {
                Object idObj = oauth2User.getAttribute("id");
                String id = idObj != null ? String.valueOf(idObj) : null;
                log.info("kakao proivderid (id):{} ", id);
                yield id;
            }
        };

        if (providerId == null) {
            throw new OAuth2AuthenticationException(
                    "Provider Id를 추출할 수 없습니다. OAuth2User: " + oauth2User.getAttributes());
        }

        return providerId;
    }

}
