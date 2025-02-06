package com.ssafy.model.entity;

import com.ssafy.model.entity.Membership;
import com.ssafy.model.entity.Room;
import com.ssafy.model.entity.User;
import com.ssafy.model.entity.MissionPosition;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TmissionCompletion {

    private Long roomId;
    private Long missionId;
    private Long userId;
    private LocalDateTime completionAt;

    // 연관
    private Room room;
    private MissionPosition missionPosition;
    private User user;
    private Membership membership;
}
