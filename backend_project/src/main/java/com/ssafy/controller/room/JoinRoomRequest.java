package com.ssafy.controller.room;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JoinRoomRequest {
    private String inviteCode;
}