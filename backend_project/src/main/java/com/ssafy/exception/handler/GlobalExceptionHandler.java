package com.ssafy.exception.handler;

import com.ssafy.controller.common.FailResponse;
import com.ssafy.exception.common.DatabaseOperationException;
import com.ssafy.exception.common.InvalidRequestException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Hidden  // Swagger 문서에서 이 클래스 제외
@Order(Ordered.LOWEST_PRECEDENCE)       // 구체적인 예외가 먼저 처리될 수 있또록 우선순위 낮게
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<FailResponse> handleDatabaseError(DatabaseOperationException e) {
        log.error("Database error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FailResponse.builder()
                .status(500).message(e.getMessage()).error("Internal Server Error").build());
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<FailResponse> handleInvalidRequest(InvalidRequestException e) {
        log.error("Invalid request: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(FailResponse.builder()
                .status(400).message(e.getMessage()).error("Bad Request").build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<FailResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Illegal argument: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(FailResponse.builder()
                        .status(400)
                        .message(e.getMessage())
                        .error("Illegal Argument")
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<FailResponse> handleGeneralException(Exception e) {
        log.error("Unhandled exception: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(FailResponse.builder()
                        .status(500)
                        .message("서버 오류가 발생했습니다.")
                        .error("Internal Server Error")
                        .build());
    }

}
