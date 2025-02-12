package com.ssafy.controller.room;

import com.ssafy.model.entity.Room;
import com.ssafy.model.entity.User;
import com.ssafy.model.service.auth.AuthService;
import com.ssafy.model.service.room.GroupService;
import com.ssafy.model.service.room.RoomService;
import com.ssafy.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Tag(name = "방", description = "방 생성 API")
public class RoomController {

    private final RoomService roomService;
    private final GroupService groupService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    @Operation(summary = "방 생성")
    @PostMapping
    public ResponseEntity<RoomResponse.Room> createRoom(
            @RequestBody RoomRequest roomRequest,
            HttpServletRequest request
    ) {
        // 1) 헤더에서 Bearer 토큰 추출
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("인증 토큰이 없습니다.");
        }
        String token = authHeader.substring(7);

        // 2) 토큰 → providerId → user 찾기
        String providerId = jwtTokenProvider.getProviderId(token);
        User user = authService.findUser(providerId);

        // 3) Service 호출 → 방 엔티티 생성
        Room room = roomService.createRoom(roomRequest, user.getUserId());

        // 4) 방 엔티티를 DTO로 변환하여 반환
        RoomResponse.Room respDto = RoomResponse.Room.builder()
                .roomId(room.getRoomId())
                .roomName(room.getRoomName())
                .location(room.getLocation())
                .inviteCode(room.getInviteCode())
                .numOfGroups(room.getNumOfGroups())
                .createdAt(room.getCreatedAt())
                .build();

        return ResponseEntity.ok(respDto);
    }

    @Operation(summary = "전체 조 및 초대코드 조회")
    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoomAndGroups(@PathVariable Long roomId) {
        // 1) 방 조회
        Room room = roomService.findRoomById(roomId);
        if (room == null) {
            return ResponseEntity.notFound().build();
        }

        // 2) groupNo 별 memberCount
        var groups = groupService.getAllGroups(room.getRoomId(), room.getNumOfGroups());

        // 3) 명세대로 JSON 구성
        Map<String,Object> data = new HashMap<>();
        data.put("roomId", room.getRoomId());
        data.put("roomName", room.getRoomName());
        data.put("inviteCode", room.getInviteCode());
        data.put("numOfGroups", room.getNumOfGroups());
        data.put("groups", groups);

        return ResponseEntity.ok(data);
    }

    // 그룹 추가
    @Operation(summary = "그룹 추가")
    @PutMapping("/{roomId}/groups/increase")
    public ResponseEntity<?> increaseGroupCount(@PathVariable Long roomId) {
        Room updatedRoom = roomService.increaseGroupCount(roomId);
        return ResponseEntity.ok(updatedRoom);
    }

}