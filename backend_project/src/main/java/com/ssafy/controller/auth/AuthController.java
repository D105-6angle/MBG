package com.ssafy.controller.auth;

import com.ssafy.exception.auth.NotFoundUserException;
import com.ssafy.model.service.auth.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@Tag(name = "로그인", description = "로그인 API")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(String providerId) {
        System.out.println("너 왔니?");
        try {
            // 정상 로그인
            LoginResponse.SuccessDto response = authService.login(providerId);
            return ResponseEntity.ok(response);
        } catch (NotFoundUserException e) {
            // 회원 정보가 없을 때
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(LoginResponse.FailDto.builder()
                            .status(204).message("회원 정보가 없습니다.")
                            .error("No Content").build());
        }
    }
}