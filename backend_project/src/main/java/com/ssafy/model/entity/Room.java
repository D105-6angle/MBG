package com.ssafy.model.entity;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {
    private Long roomId;
    private Long teacherId;
    private String location;
    private String roomName;
    private int numOfGroups;
    private String inviteCode;
    private LocalDateTime createdAt;
    private boolean status;
}
