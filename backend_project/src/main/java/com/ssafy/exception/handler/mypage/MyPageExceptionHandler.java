package com.ssafy.exception.handler.mypage;

import com.ssafy.controller.common.FailResponse;
import com.ssafy.exception.mypage.NotFoundCardException;
import com.ssafy.exception.mypage.NotFoundProblemException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = {"com.ssafy.controller.mypage", "com.ssafy.controller.upload"})
@Order(Ordered.HIGHEST_PRECEDENCE)
@Hidden
@Slf4j
public class MyPageExceptionHandler {

    @ExceptionHandler(NotFoundCardException.class)
    public ResponseEntity<FailResponse> handleNotFoundCard(NotFoundCardException e) {
        log.error("Card not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(FailResponse.builder()
                .status(404).message(e.getMessage()).error("RESOURCE_NOT_FOUND").build());
    }

    @ExceptionHandler(NotFoundProblemException.class)
    public ResponseEntity<FailResponse> handleNotFoundProblem(NotFoundProblemException e) {
        log.error("Problem not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(FailResponse.builder()
                .status(404).message(e.getMessage()).error("RESOURCE_NOT_FOUND").build());
    }
}
