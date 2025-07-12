package com.happyhappy.backend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.happyhappy.backend.authentication.provider.TokenProvider;
import com.happyhappy.backend.member.dto.MemberDetails;
import com.happyhappy.backend.member.enums.RoleType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class JwtAuthenticationTest {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String TOKEN_PREFIX = "Bearer ";

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("옳은 JWT로 인증되어야 한다.")
    void 옳은_JWT로_인증되어야_한다() throws Exception {
        //given
        List<GrantedAuthority> authroties = List.of(
                new SimpleGrantedAuthority(RoleType.USER.getAuthority()));
        MemberDetails memberDetails = new MemberDetails(UUID.randomUUID(), "test", "",
                authroties);
        Authentication authentication = new UsernamePasswordAuthenticationToken(memberDetails, "",
                authroties);
        String accessToken = tokenProvider.generateAccessToken(authentication);

        //when
        MvcResult result = mockMvc.perform(
                get("/shoudNotFound").header(AUTHORIZATION_HEADER,
                        String.format("%s%s", TOKEN_PREFIX, accessToken))).andReturn();

        //then
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("JWT 토큰 없이 보호된 API에 접근하면 401 에러가 발생한다")
    void JWT토큰_없이_보호된_API에_접근하면_401에러가_발생한다() throws Exception {
        // when
        MvcResult result = mockMvc.perform(get("/api/secret"))
                .andReturn();

        System.out.println("실제 상태 코드:" + result.getResponse().getStatus());
        // then
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("잘못된 JWT 토큰으로 보호된 API에 접근하면 401 에러가 발생한다")
    void 잘못된_JWT토큰으로_보호된_API에_접근하면_401에러가_발생한다() throws Exception {
        // given
        String invalidToken = "invalid.jwt.token";

        // when
        MvcResult result = mockMvc.perform(
                get("/api/protected")
                        .header(AUTHORIZATION_HEADER,
                                String.format("%s%s", TOKEN_PREFIX, invalidToken))).andReturn();

        // then
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    private String createValidAccessTokenWithEmail(String email) {
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(RoleType.USER.getAuthority()));

        MemberDetails memberDetails = new MemberDetails(UUID.randomUUID(), email, "",
                authorities);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                email, "", authorities);

        return tokenProvider.generateAccessToken(authentication);
    }

}
