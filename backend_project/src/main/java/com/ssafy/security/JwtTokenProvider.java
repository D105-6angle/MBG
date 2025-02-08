package com.ssafy.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
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

    /* 토큰 만료 여부 검사 */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes())).build().parseClaimsJws(token).getBody();
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {       // 만료일이 다 된 경우
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            // 1. parserBuilder()로 JWT 파서 생성
            Jwts.parserBuilder()
                    // 2. JWT를 생성할 때 사용한 동일한 비밀키 설정
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    // 3. 파서된 내용 빌드
                    .build()
                    // 4. JWT 파싱 시도 (서명 검증, 토큰 만료 여부 확인, 토큰 형식 검증)
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | SecurityException | MalformedJwtException e) {
            // 1)토큰이 만료된 경우, 2)서명이 유효하지 않은 경우, 3) 토큰 형식이 잘못된 경우
            return false;
        } catch (Exception e) {
            // 그 외
            return false;
        }
    }

    /* 토큰에서 Authenication 객체 추출 */
    public Authentication getAuthentication(String token) {
        String providerId = getProviderId(token);      // 토큰에서 필요한 정보(ProviderId) 추출
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
