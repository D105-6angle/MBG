package com.ssafy.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
public class JWTUtil {

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
}
