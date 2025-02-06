package com.ssafy.schedules.contorller;

import com.ssafy.common.dto.ApiResponse;
import com.ssafy.schedules.dto.request.ScheduleRequest;
import com.ssafy.schedules.dto.response.ScheduleListResponse;
import com.ssafy.schedules.dto.response.ScheduleResponse;
import com.ssafy.schedules.exception.ScheduleNotFoundException;
import com.ssafy.schedules.exception.RoomNotFoundException;
import com.ssafy.schedules.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "일정 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 방",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping
    public ResponseEntity<?> getSchedules(@PathVariable Long roomId) {
        try {
            ScheduleListResponse schedules = scheduleService.getSchedulesByRoomId(roomId);
            return ResponseEntity.ok(schedules);
        } catch (RoomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status(404)
                            .message("존재하지 않는 방입니다.")
                            .build());
        }
    }

    @Operation(summary = "일정 생성")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "일정 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 방",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping
    public ResponseEntity<?> createSchedule(
            @PathVariable Long roomId,
            @RequestBody ScheduleRequest request) {
        try {
            ScheduleResponse schedule = scheduleService.createSchedule(roomId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(schedule);
        } catch (RoomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status(404)
                            .message("존재하지 않는 방입니다.")
                            .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status(400)
                            .message(e.getMessage())
                            .build());
        }
    }

    @Operation(summary = "일정 삭제")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "일정 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 방 또는 일정",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<?> deleteSchedule(
            @PathVariable Long roomId,
            @PathVariable Long scheduleId) {
        try {
            scheduleService.deleteSchedule(roomId, scheduleId);
            return ResponseEntity.ok().build();
        } catch (RoomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status(404)
                            .message("존재하지 않는 방입니다.")
                            .build());
        } catch (ScheduleNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status(404)
                            .message("존재하지 않는 일정입니다.")
                            .build());
        }
    }

    @Operation(summary = "일정 수정")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "일정 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 방 또는 일정",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PutMapping("/{scheduleId}")
    public ResponseEntity<?> updateSchedule(
            @PathVariable Long roomId,
            @PathVariable Long scheduleId,
            @RequestBody ScheduleRequest request) {
        try {
            ScheduleResponse schedule = scheduleService.updateSchedule(roomId, scheduleId, request);
            return ResponseEntity.ok(schedule);
        } catch (RoomNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status(404)
                            .message("존재하지 않는 방입니다.")
                            .build());
        } catch (ScheduleNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.builder()
                            .status(404)
                            .message("존재하지 않는 일정입니다.")
                            .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.builder()
                            .status(400)
                            .message(e.getMessage())
                            .build());
        }
    }
        }