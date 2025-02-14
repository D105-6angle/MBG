package com.ssafy.config;

import com.ssafy.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration              // Spring의 설정 클래스
@EnableWebSecurity          // Spring Security 활성화
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http    // 1. CSRF 보호 기능 비활성화 (REST API에서는 CSRF 보호가 필요없음 -> 쿠키 기반 인증을 사용하지 않으므로)
                .csrf(csrf -> csrf.disable())
                // 2. 세션 관리 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))   // 서버에 세션을 생성하지 않음 (JWT 사용하므로)
                // 3. URL별 접근 권한 설정
                .authorizeHttpRequests(authorize -> authorize
                        // Swagger UI 관련 경로는 누구나 접근 가능
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // 로그인, 회원가입 등의 인증 관련 경로는 누구나 접근 가능
                        .requestMatchers("/api/auth/**").permitAll()
                        // multipart 요청에 대한 명시적 허용
                        .requestMatchers(HttpMethod.POST, "/api/upload/**").authenticated()
                        // 그 외 모든 요청은 인증이 필요
                        .anyRequest().authenticated())
                // 4. UsernamePasswordAuthenticationFilter 이전에 우리가 만든 JwtAuthenticationFilter가 동작하도록 앞에다가 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
