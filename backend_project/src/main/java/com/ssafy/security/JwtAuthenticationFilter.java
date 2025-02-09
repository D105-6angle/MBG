package com.ssafy.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    public static final String JWT_HEADER_NAME = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. JWT 토큰 추출
        String token = extrectToken(request);
        // 2. JWT 토큰 내용이 존재하고, 유효한 토큰인지 검사
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            // 유효한 토큰이라면 사용자 인증 정보 객체를 생성해서 SecurityContext에 등록 (그러면 요청이 처리되어 완료될 때까지 전역적으로 접근이 가능)
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 3. 등록된 다음 필터로 클라이언트 요청을 전달
        filterChain.doFilter(request, response);
    }

    /* 클라이언트 요청 헤더에서 JWT 토큰 추출 */
    private String extrectToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(JWT_HEADER_NAME);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JWT_PREFIX)) {
            return bearerToken.substring(JWT_PREFIX.length());
        }

        return null;
    }
}
