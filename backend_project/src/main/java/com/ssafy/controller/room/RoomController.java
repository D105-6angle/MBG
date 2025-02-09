package com.ssafy.controller.room;

import com.ssafy.model.entity.User;
import com.ssafy.model.service.auth.AuthService;
import com.ssafy.model.service.room.RoomService;
import com.ssafy.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
    private final JwtTokenProvider jwtTokenProvider; // JWT 토큰 파싱용 Bean
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<RoomResponse.Room> createRoom(@RequestBody RoomRequest roomRequest,
                                                        HttpServletRequest request) {

        // HTTP 헤더의 Authorization에서 JWT 토큰 추출 ("Bearer {token}" 형식)
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("인증 토큰이 없습니다.");
        }
        String token = authHeader.substring(7);

        // JwtTokenProvider -> providerId
        String providerId = jwtTokenProvider.getProviderId(token);

        // providerId → teacherId
        User user = authService.findUser(providerId);
        Long teacherId = user.getUserId();

        // RoomService에 teacherId를 전달 -> 방 생성
        RoomResponse.Room roomResponse = roomService.createRoom(roomRequest, teacherId);
        return ResponseEntity.ok(roomResponse);
    }
}