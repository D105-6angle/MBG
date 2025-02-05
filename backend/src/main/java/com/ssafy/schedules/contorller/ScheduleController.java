package com.ssafy.schedules.contorller;

import com.ssafy.common.dto.ApiResponse;
import com.ssafy.schedules.dto.request.ScheduleRequest;
import com.ssafy.schedules.dto.response.ScheduleListResponse;
import com.ssafy.schedules.dto.response.ScheduleResponse;
import com.ssafy.schedules.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/rooms/{roomId}/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<ScheduleListResponse> getSchedules(@PathVariable Long roomId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByRoomId(roomId));
    }

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
}
