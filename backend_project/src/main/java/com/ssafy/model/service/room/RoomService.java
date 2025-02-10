package com.ssafy.model.service.room;

import com.ssafy.controller.room.RoomRequest;
import com.ssafy.controller.room.RoomResponse;
import com.ssafy.exception.common.DatabaseOperationException;
import com.ssafy.exception.common.InvalidRequestException;
import com.ssafy.model.entity.Room;
import com.ssafy.model.mapper.room.RoomMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomMapper roomMapper;
    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);

    public RoomResponse.Room createRoom(RoomRequest request, Long teacherId) {
        // 필수 항목 검증
        if (request.getRoomName() == null || request.getRoomName().trim().isEmpty() ||
                request.getLocation() == null || request.getLocation().trim().isEmpty() ||
                request.getNumOfGroups() <= 0) {
            throw new InvalidRequestException("잘못된 요청입니다. 필수 항목을 확인해주세요.");
        }

        // 초대 코드 생성 (UUID의 앞 8자리)
        String inviteCode = generateInviteCode();

        // Room 엔티티 생성
        Room room = Room.builder()
                .roomName(request.getRoomName())
                .location(request.getLocation())
                .numOfGroups(request.getNumOfGroups())
                .inviteCode(inviteCode)
                .createdAt(LocalDateTime.now())
                .teacherId(teacherId)
                .status(true)
                .build();

        // 방 생성
        try {
            roomMapper.insertRoom(room);
        } catch (Exception e) {
            throw new DatabaseOperationException("방 생성 중 DB 작업에 실패했습니다.");
        }

        logger.info("Room {} 생성됨. 그룹 번호: 1 ~ {}", room.getRoomId(), room.getNumOfGroups());

        // RoomResponse 객체 생성
        return RoomResponse.Room.builder()
                .roomId(room.getRoomId())
                .roomName(room.getRoomName())
                .location(room.getLocation())
                .inviteCode(room.getInviteCode())
                .numOfGroups(room.getNumOfGroups())
                .createdAt(room.getCreatedAt())
                .build();
    }

    // 초대코드 생성 메서드
    private String generateInviteCode() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
