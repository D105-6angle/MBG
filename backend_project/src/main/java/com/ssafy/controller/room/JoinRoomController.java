package com.ssafy.controller.room;

import com.ssafy.model.entity.Room;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ssafy.model.service.room.RoomService;

import java.util.Map;

@RestController
@RequestMapping("/api/rooms/join")
@RequiredArgsConstructor
@Tag(name = "방", description = "학생 방 입장 API")
public class JoinRoomController {

    private final RoomService roomService;

    @Operation(summary = "초대코드 입력하여 방 참여")
    @PostMapping
    public ResponseEntity<?> joinRoom(@RequestBody JoinRoomRequest request) {
//        System.out.println("초대코드: " + request.getInviteCode());

        Room room = roomService.joinRoom(request.getInviteCode());
        JoinRoomResponse response = JoinRoomResponse.builder()
                .roomId(room.getRoomId())
                .inviteCode(room.getInviteCode())
                .build();
        return ResponseEntity.ok(response);
    }
}

