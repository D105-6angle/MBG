package com.ssafy.schedules.contorller;

import com.ssafy.common.dto.ApiResponse;
import com.ssafy.schedules.dto.request.ScheduleRequest;
import com.ssafy.schedules.dto.response.ScheduleListResponse;
import com.ssafy.schedules.dto.response.ScheduleResponse;
import com.ssafy.schedules.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/rooms/{roomId}/schedules")
@RequiredArgsConstructor
@Tag(name = "일정", description = "일정관리 API")

public class ScheduleController {
    private final ScheduleService scheduleService;
    @Operation(summary = "일정 전체 조회")
    @GetMapping
    public ResponseEntity<ScheduleListResponse> getSchedules(@PathVariable Long roomId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByRoomId(roomId));
    }

    @Operation(summary = "일정 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<ScheduleResponse>> createSchedule(
            @PathVariable Long roomId,
            @RequestBody ScheduleRequest request) {

        ScheduleResponse schedule = scheduleService.createSchedule(roomId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ScheduleResponse>builder()
                        .status(201)
                        .message("스케줄 생성 성공")
                        .data(schedule)
                        .build());

    }

    @Operation(summary = "일정 삭제")
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(
            @PathVariable Long roomId,
            @PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(roomId, scheduleId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(200)
                .message("스케줄 삭제 성공")
                .build());
    }

    @Operation(summary = "일정 수정")
    @PutMapping("/{scheduleId}")
    public ResponseEntity<ApiResponse<ScheduleResponse>> updateSchedule(
            @PathVariable Long roomId,
            @PathVariable Long scheduleId,
            @RequestBody ScheduleRequest request) {
        return ResponseEntity.ok(ApiResponse.<ScheduleResponse>builder()
                .status(200)
                .message("스케줄 수정 성공")
                .data(scheduleService.updateSchedule(roomId, scheduleId, request))
                .build());
    }
}
