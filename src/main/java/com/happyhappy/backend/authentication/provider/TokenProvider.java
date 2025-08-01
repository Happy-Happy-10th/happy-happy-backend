package com.happyhappy.backend.authentication.provider;

import com.happyhappy.backend.member.dto.MemberDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenValidityInMilliSeconds;
    private final long refreshTokenValidityInMilliSeconds;

    public TokenProvider(@Value("${swyp.jwt.secret}") String secretKey,
                         @Value("${swyp.jwt.access-token-validity-in-milli-seconds}")
                         long accessTokenValidityInMilliSeconds,
                         @Value("${swyp.jwt.refresh-token-validity-in-milli-seconds}")
                         long refreshTokenValidityInMilliSeconds) {

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidityInMilliSeconds = accessTokenValidityInMilliSeconds;
        this.refreshTokenValidityInMilliSeconds = refreshTokenValidityInMilliSeconds;
    }

    // Access Token 생성
    public String generateAccessToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        MemberDetails principal = (MemberDetails) authentication.getPrincipal();

        long now = (new Date()).getTime();
        Date accessTokenExpiration = new Date(now + accessTokenValidityInMilliSeconds);

        return Jwts.builder()
                .subject(authentication.getName())
                .claim("auth", authorities)
                .claim("user", principal.getMemberId())
                .expiration(accessTokenExpiration)
                .signWith(secretKey)
                .compact();
    }

    // Refresh token 생성
    public String generateRefreshToken(Authentication authentication) {
        long now = (new Date()).getTime();
        Date refreshTokenExpiration = new Date(now + refreshTokenValidityInMilliSeconds);

        return Jwts.builder()
                .subject(authentication.getName())
                .issuedAt(new Date(now))
                .expiration(refreshTokenExpiration)
                .signWith(secretKey)
                .compact();
    }


    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("[토큰 검증 중 에러] 유효하지 않은 jwt입니다.");
        } catch (ExpiredJwtException e) {
            log.info("[토큰 검증 중 에러] 만료된 jwt입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("[토큰 검증 중 에러] 지원하지 않는 jwt입니다.");
        } catch (IllegalArgumentException e) {
            log.info("[토큰 검증 중 에러] jwt claims 문자열이 빈 값 입니다.");
        }
        return false;
    }

    /**
     * 토큰에서 Authentication 객체 생성 (Filter에서 사용)
     */
    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);

        // TODO: 나중에 권한 처리 개선 필요 (현재는 기본 권한만)
        // 리프레시 토큰 작업 완료 후 권한 로직 추가

        return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                email, null, Collections.emptyList() // 권한 빈 리스트
        );
    }

    /**
     * 토큰에서 사용자 이메일 추출
     */
    public String getEmailFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    /**
     * 토큰 만료 여부
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = parseClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public Date extractExpiration(String token) {
        return Jwts.parser() // parserBuilder()가 아니라 parser() 사용
                .verifyWith(secretKey) // 서명 키 설정
                .build()
                .parseSignedClaims(token) // parseClaimsJws() 대신
                .getPayload()
                .getExpiration();
    }
}
