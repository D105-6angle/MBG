//package com.ssafy.exception.handler;
//
//
//import com.ssafy.exception.report.DuplicateReportException;
//import com.ssafy.exception.schedule.RoomNotFoundException;
//import io.swagger.v3.oas.annotations.Hidden;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestControllerAdvice
//@Hidden
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(RoomNotFoundException.class)
//    public ResponseEntity<Map<String, Object>> handleRoomNotFoundException(RoomNotFoundException e) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", e.getMessage());
//        response.put("error", "Not Found");
//        response.put("status", HttpStatus.NOT_FOUND.value());
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//    }
//
//    @ExceptionHandler(DuplicateReportException.class)
//    public ResponseEntity<Map<String, Object>> handleDuplicateReportException(DuplicateReportException e) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", e.getMessage());
//        response.put("error", "Conflict");
//        response.put("status", HttpStatus.CONFLICT.value());
//        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
//    }
//}