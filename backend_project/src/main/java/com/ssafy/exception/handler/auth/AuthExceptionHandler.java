package com.ssafy.exception.handler.auth;

import com.ssafy.controller.common.FailResponse;
import com.ssafy.exception.auth.DuplicateUserException;
import com.ssafy.exception.auth.InvalidTokenException;
import com.ssafy.exception.auth.NotFoundUserException;
import com.ssafy.exception.auth.WithdrawnUserException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.ssafy.controller.auth")
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@Hidden  // Swagger 문서에서 이 클래스 제외
public class AuthExceptionHandler {

    @ExceptionHandler(NotFoundUserException.class)
    public ResponseEntity<FailResponse> handleNotFoundUser(NotFoundUserException e) {
        log.error("User not found: {}", e.getMessage());
        log.debug("Error details:", e);  // 스택 트레이스도 함께 기록
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(FailResponse.builder()
                .status(204).message(e.getMessage()).error("No Content").build());
    }

    @ExceptionHandler(WithdrawnUserException.class)
    public ResponseEntity<FailResponse> handleWithdrawnUser(WithdrawnUserException e) {
        log.error("Withdrawn user: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FailResponse.builder()
                .status(403).message(e.getMessage()).error("Forbidden").build());
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<FailResponse> handleDuplicateUser(DuplicateUserException e) {
        log.error("Duplicate user: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(FailResponse.builder()
                .status(409).message(e.getMessage()).error("Conflict").build());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<FailResponse> handleInvalidToken(InvalidTokenException e) {
        log.error("Invalid token: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(FailResponse.builder().status(401).message(e.getMessage()).error("Unauthorized").build());
    }
}
