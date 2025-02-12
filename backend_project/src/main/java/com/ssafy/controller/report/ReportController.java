package com.ssafy.controller.report;

import com.ssafy.model.service.report.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "만족도 조사", description = "보고서와 관련된 API")
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "설문 결과 조회", description = "방 ID로 설문 결과를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "설문 결과 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 방입니다.")
    })
    @GetMapping("/{roomId}")
    public ResponseEntity<ReportResponse> getReportsByRoomId(@PathVariable Long roomId) {
        return ResponseEntity.ok(reportService.getReportsByRoomId(roomId));
    }

//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException e) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("status", HttpStatus.NOT_FOUND.value());
//        response.put("message", "존재하지 않는 방입니다.");
//        response.put("error", "Not Found");
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//    }
    @Operation(summary = "만족도 조사 설문 제출", description = "학생 개별 만족도 조사 설문 제출")
    @PostMapping
    public ResponseEntity<Void> submitReport(@Valid @RequestBody ReportRequest request) {
        reportService.submitReport(request);
        return ResponseEntity.ok().build();
    }
}