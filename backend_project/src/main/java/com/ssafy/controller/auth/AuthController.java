package com.ssafy.controller.auth;

import com.ssafy.exception.common.InvalidRequestException;
import com.ssafy.model.service.auth.AuthService;
import com.ssafy.security.JwtAuthenticationFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "인증/인가", description = "회원가입, 로그인, 토큰 재발급과 관련된 API")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    @Value("${app.environment}")
    private String activeProfile;
    private final AuthService authService;

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest.LoginData loginData) {
        String providerId = loginData.getProviderId();
        AuthResponse.SuccessDto response = authService.login(providerId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원가입")
    @PostMapping("/register")
    public ResponseEntity<?> regist(@RequestBody AuthRequest.UserInfo userInfo, HttpServletRequest request) {
        String typeCode = getTypeCode(request);
        AuthResponse.SuccessDto response = authService.regist(userInfo, typeCode);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "토큰 재발급", description = "토큰이 만료되었을 때 재발급 받는 API")
    @PostMapping("reissue")
    public ResponseEntity<?> reissue(@RequestHeader("Authorization") String refreshToken, @AuthenticationPrincipal String providerId) {
        refreshToken = refreshToken.substring(JwtAuthenticationFilter.JWT_PREFIX.length());     // Prefix(Bearer) 제거
        AuthResponse.SuccessDto response = authService.reissue(providerId, refreshToken);
        return ResponseEntity.ok(response);
    }

    private String getTypeCode(HttpServletRequest request) {
        if ("prod".equals(activeProfile)) {
            String typeCode = request.getHeader("X-App-Type");
            if (typeCode == null || typeCode.isEmpty()) {
                throw new InvalidRequestException("공통 코드가 비었거나 없습니다.");
            }
            return typeCode;
        }
        return "U001";      // 테스트 환경에선 학생으로 회원가입하자.
    }
}