package com.ssafy.team.model;

import com.ssafy.group.model.Membership;
import com.ssafy.group.model.Room;
import com.ssafy.mypage.model.User;
import com.ssafy.mission.model.MissionPosition;

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
