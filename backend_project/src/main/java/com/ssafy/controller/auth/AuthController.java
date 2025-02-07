package com.ssafy.controller.auth;

import com.ssafy.exception.auth.DuplicateUserException;
import com.ssafy.exception.auth.InvalidInputException;
import com.ssafy.exception.auth.NotFoundUserException;
import com.ssafy.model.service.auth.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "로그인", description = "로그인 API")
@RequiredArgsConstructor
public class AuthController {
    @Value("${app.environment}")
    private String activeProfile;

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(String providerId) {
        try {
            AuthResponse.SuccessDto response = authService.login(providerId);
            return ResponseEntity.ok(response);
        } catch (NotFoundUserException e) {     // 회원 정보가 없을 때
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(AuthResponse.FailDto.builder()
                            .status(204).message("회원 정보가 없습니다.")
                            .error("No Content").build());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> regist(@RequestBody AuthRequest.UserInfoData userInfo, HttpServletRequest request) {
        try {
            String typeCode;        // 학생인지 선생님인지에 대한 공통 코드

            /* -------실제 서비스 코드------- */
            if ("prod".equals(activeProfile)) {
                typeCode = request.getHeader("X-App-Type");
                if (typeCode == null || typeCode.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                                .body(AuthResponse.FailDto.builder().status(400).error("Bad Request")
                                                                .message("공통 코드가 비었거나 없습니다.").build());
            } else {
                /*----------- 테스트코드 -----------*/
                typeCode = "TestCode";
            }

            AuthResponse.SuccessDto response = authService.regist(userInfo, typeCode);
            return ResponseEntity.ok(response);
        } catch (DuplicateUserException e) {    // 이미 가입한 회원일 경우
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(AuthResponse.FailDto.builder().status(409).error("Conflict").message("이미 가입한 회원입니다.").build());
        } catch (InvalidInputException e) {    // 필수 정보 누락
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.FailDto.builder().status(400).error("Bad Request")
                            .message("필수 정보가 누락되었습니다.").build());
        }
    }
}