package com.happyhappy.backend.common.config;


import com.happyhappy.backend.authentication.filter.JwtAuthenticationFilter;
import com.happyhappy.backend.authentication.handler.CustomAccessDeniedHandler;
import com.happyhappy.backend.authentication.handler.CustomAuthenticationEntryPoint;
import com.happyhappy.backend.authentication.handler.CustomOAuth2SuccessHandler;
import com.happyhappy.backend.authentication.provider.TokenProvider;
import com.happyhappy.backend.member.service.MemberOAuth2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
            MemberOAuth2Service memberOAuth2Service)
            throws Exception {
        return http
                .exceptionHandling(configurer -> configurer
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                        .authenticationEntryPoint(
                                new CustomAuthenticationEntryPoint(handlerExceptionResolver))
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(configurer -> configurer.sessionCreationPolicy(
                        SessionCreationPolicy.IF_REQUIRED))

                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(memberOAuth2Service))
                        .successHandler(customOAuth2SuccessHandler)
                        .failureHandler((request, response, exception) -> {
                            log.error("OAuth 로그인 실패", exception);
                            response.sendRedirect(
                                    "http://localhost:3000/oauth/callback?error=oauth_failed&success=false");
                        })
                )
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/oauth2/**").permitAll()
                        .requestMatchers("/oauth/**").permitAll()
                        .requestMatchers("/login/oauth2/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/error/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenProvider);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
