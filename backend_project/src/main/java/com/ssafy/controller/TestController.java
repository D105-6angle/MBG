package com.ssafy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/protected")
    public ResponseEntity<String> protectedEndPoint() {
        // SecurityContext에서 현재 인증된 사용자 정보를 가져옴
        // SecurityContext는 ThreadLocal 기반이기 때문에 각 사용자의 요청 별로 독립적으로 동작
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String providerId = authentication.getName();

        // 인증된 유저가 접근하면 200 OK, 토큰이 없거나 잘못된 토큰이라면 403 Forbidden이 뜬다.
        return ResponseEntity.ok("보호된 엔드포인트에 접근한 유저: " + providerId);
    }
}
