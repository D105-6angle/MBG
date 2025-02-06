package com.ssafy.model.service.room;

import com.ssafy.controller.room.RoomRequest;
import com.ssafy.controller.room.RoomResponse;
import com.ssafy.model.entity.Room;
import com.ssafy.model.mapper.room.RoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomMapper roomMapper;

    public RoomResponse.Room createRoom(Long teacherId, String teacherName, RoomRequest request) {
        Room room = Room.builder()
                .teacherId(teacherId)
                .roomName(request.getRoomName())
                .location(request.getLocation())
                .numOfGroups(request.getNumOfGroups())
                .inviteCode(generateInviteCode())
                .createdAt(LocalDateTime.now())
                .status(true)
                .build();

        roomMapper.insertRoom(room);

        return toRoomResponse(room, teacherId, teacherName);
    }

    //초대 코드
    private String generateInviteCode() {
        return "INV" + System.currentTimeMillis();
    }

    private RoomResponse.Room toRoomResponse(Room room, Long teacherId, String teacherName) {
        return RoomResponse.Room.builder()
                .roomId(room.getRoomId())
                .roomName(room.getRoomName())
                .location(room.getLocation())
                .inviteCode(room.getInviteCode())
                .numOfGroups(room.getNumOfGroups())
                .createdAt(room.getCreatedAt())
                .teacher(RoomResponse.Teacher.builder()
                        .teacherId(teacherId)
                        .name(teacherName)
                        .build())
                .build();
    }
}
