package com.ssafy.exception.handler.room;

import com.ssafy.controller.common.FailResponse;
import com.ssafy.exception.room.GroupNotFoundException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.ssafy.controller.room.group")
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@Hidden
public class GroupExceptionHandler {

    @ExceptionHandler(GroupNotFoundException.class)
    public ResponseEntity<FailResponse> handleGroupNotFound(GroupNotFoundException e) {
        log.error("Group not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(FailResponse.builder()
                        .status(404)
                        .message(e.getMessage())
                        .error("Group Not Found")
                        .build());
    }
}
