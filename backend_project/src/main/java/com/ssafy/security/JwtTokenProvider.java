package com.ssafy.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Collections;
import java.util.Date;


/* JWT 토큰을 발급해주는 클래스 */
@Component
public class JwtTokenProvider {
    // application-dev.properties에 있는 JWT secret Key 값
    @Value("${jwt.secret}")
    private String secretKey;

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;            // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 14; // 14일

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createAccessToken(String providerId) {
        Claims claims = Jwts.claims();
        claims.setSubject(providerId);      // 사용자를 구별할 수 있는 정보

        // Access Token 만료일
        Date now = new Date();
        Date expiration = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        // Access Token 생성
        return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(expiration)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes())).compact();
    }

    public String createRefreshToken(String providerId) {
        Claims claims = Jwts.claims();
        claims.setSubject(providerId);

        Date now = new Date();
        Date expiration = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    public String getProviderId(String token) {
        return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build().parseClaimsJws(token).getBody().getSubject();
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /* 토큰에서 Authenication 객체 추출 */
    public Authentication getAuthentication(String token) {
        // 토큰에서 필요한 정보 추출
        String providerId = getProviderId(token);
        // 모든 인증된 사용자에게 기본 권한 부여 -> 추후 역할(ex. 학생, 선생님, 관리자)에 따라 인가를 별도로 줄 수 있기도 함
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");

        /*
            UsernamePasswordAuthenticationToken
            (principal, credentials, authorities)

            - principal: 사용자 식별 정보
            - credentails: 비밀번호나 JWT를 보통 사용하므로 null
            - authorities: 권한 목록
         */

        // 여러 권한 목록 객체가 생성될 필요가 없으므로 Singleton으로
        return new UsernamePasswordAuthenticationToken(providerId, null, Collections.singleton(authority));
    }
}
