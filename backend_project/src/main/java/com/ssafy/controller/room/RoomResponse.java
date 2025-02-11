package com.ssafy.controller.room;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class RoomResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Room {
        private Long roomId;
        private String roomName;
        private String location;
        private String inviteCode;
        private int numOfGroups;
        private LocalDateTime createdAt;
    }

}