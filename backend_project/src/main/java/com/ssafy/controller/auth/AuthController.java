package com.ssafy.controller.auth;

import com.ssafy.exception.auth.DuplicateUserException;
import com.ssafy.exception.auth.InvalidInputException;
import com.ssafy.exception.auth.InvalidTokenException;
import com.ssafy.exception.auth.NotFoundUserException;
import com.ssafy.exception.common.DatabaseOperationException;
import com.ssafy.model.service.auth.AuthService;
import com.ssafy.security.JwtAuthenticationFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Description;
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

    @Operation(summary = "로그인", description = "소셜에서 받은 Provider 값을 토대로 로그인 시도하는 API")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest.LoginData loginData) {
        try {
            String providerId = loginData.getProviderId();
            AuthResponse.SuccessDto response = authService.login(providerId);
            return ResponseEntity.ok(response);
        } catch (NotFoundUserException e) {     // 회원 정보가 없을 때
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(AuthResponse.FailDto.builder()
                            .status(204).message("회원 정보가 없습니다.")
                            .error("No Content").build());
        }
    }

    @Operation(summary = "회원가입", description = "소셜에서 받은 Provider 값을 토대로 회원가입을 진행하는 API")
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

    @Operation(summary = "토큰 재발급", description = "토큰이 만료되었을 때 재발급 받는 API")
    @PostMapping("reissue")
    public ResponseEntity<?> reissue(@RequestHeader("Authorization")  String refreshToken) {
        try {
            // Prefix(Bearer) 제거
            refreshToken = refreshToken.substring(JwtAuthenticationFilter.JWT_PREFIX.length());
            AuthResponse.SuccessDto response = authService.reissue(refreshToken);
            return ResponseEntity.ok(response);
        } catch (InvalidTokenException e) {         // 유효하지 않은 토큰일 경우
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.FailDto.builder().status(401).message(e.getMessage())
                            .error("Unauthorized").build());
        } catch (DatabaseOperationException e) {    // DB에 refresh Token 저장 중 오류 발생
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(AuthResponse.FailDto.builder()
                    .status(500).message("토큰 재발급 중 오류가 발생했습니다.").error("Internal Server Error").build());
        }
    }
}