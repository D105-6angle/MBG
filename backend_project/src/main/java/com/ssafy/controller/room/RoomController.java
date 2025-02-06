package com.ssafy.controller.room;

import com.ssafy.model.service.room.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Tag(name = "방", description = "방 생성 API")
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "방 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "방 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping
    public ResponseEntity<RoomResponse.Room> createRoom(
            @Valid @RequestBody RoomRequest request, HttpServletRequest httpRequest) {

        // JWT에서 teacherId 및 teacherName 추출
        Long teacherId = (Long) httpRequest.getAttribute("teacherId");
        String teacherName = (String) httpRequest.getAttribute("teacherName");

        if (teacherId == null || teacherName == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(roomService.createRoom(teacherId, teacherName, request));
    }
}
