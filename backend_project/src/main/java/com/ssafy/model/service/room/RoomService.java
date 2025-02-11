package com.ssafy.model.service.room;

import com.ssafy.controller.room.RoomRequest;
import com.ssafy.exception.common.DatabaseOperationException;
import com.ssafy.exception.common.InvalidRequestException;
import com.ssafy.model.entity.Room;
import com.ssafy.model.mapper.room.RoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomMapper roomMapper;

    public Room createRoom(RoomRequest request, Long teacherId) {
        // 유효성 검증
        if (request.getRoomName() == null || request.getRoomName().isBlank() ||
                request.getLocation() == null || request.getLocation().isBlank() ||
                request.getNumOfGroups() <= 0) {
            throw new InvalidRequestException("방 생성 요청이 올바르지 않습니다.");
        }

        // 초대 코드 생성
        String inviteCode = UUID.randomUUID().toString().substring(0, 8);

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

        // DB insert
        int rows = roomMapper.insertRoom(room);
        if (rows == 0) {
            throw new DatabaseOperationException("방 생성 실패");
        }

        return room;
    }

    public Room findRoomById(Long roomId) {
        return roomMapper.selectRoomById(roomId);
    }


    // 초대코드로 방 입장
    public Room joinRoom(String inviteCode) {
        Room room = roomMapper.selectRoomByInviteCode(inviteCode);
        if (room == null) {
            throw new InvalidRequestException("유효하지 않은 초대코드입니다.");
        }
        return room;
    }

    // 그룹 수 추가
    public Room increaseGroupCount(Long roomId) {
        Room room = roomMapper.selectRoomById(roomId);
        if (room == null) {
            throw new InvalidRequestException("방이 존재하지 않습니다.");
        }
        int newGroupCount = room.getNumOfGroups() + 1;
        room.setNumOfGroups(newGroupCount);
        int rows = roomMapper.updateRoomGroupCount(room);
        if (rows == 0) {
            throw new DatabaseOperationException("그룹 수 업데이트 실패");
        }
        return room;
    }

}
