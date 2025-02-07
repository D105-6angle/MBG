package com.ssafy.controller.room;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RoomResponse {

    @Getter
    @Builder
    public static class Room{
        private Long roomId;
        private String roomName;
        private String location;
        private String inviteCode;
        private int numOfGroups;
        private LocalDateTime createdAt;
        private Teacher teacher;
    }

    @Getter
    @Builder
    public static class Teacher {
        private Long teacherId;
        private String name;
    }

}