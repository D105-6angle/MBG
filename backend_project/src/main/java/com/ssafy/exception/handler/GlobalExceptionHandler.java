package com.ssafy.exception.handler;


import com.ssafy.controller.common.FailResponse;
import com.ssafy.exception.report.DuplicateReportException;
import com.ssafy.exception.s3.NullPresignedURLException;
import com.ssafy.exception.schedule.RoomNotFoundException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Hidden
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(NullPresignedURLException.class)
    public ResponseEntity<FailResponse> handleNullPresignedURL(NullPresignedURLException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FailResponse.builder()
                .status(500).message(e.getMessage()).error("Null").build());
    }
}