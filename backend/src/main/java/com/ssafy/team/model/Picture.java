package com.ssafy.team.model;

import com.ssafy.group.model.Membership;
import com.ssafy.group.model.Room;
import com.ssafy.mission.model.MissionPosition;
import com.ssafy.mypage.model.User;
import lombok.*;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Picture {
    private Long pictureId;
    private Long roomId;
    private Long userId;
    private Long missionId;
    private String pictureUrl;
    private LocalDateTime completionTime;

    // 연관
    private Room room;
    private User user;
    private MissionPosition mission;
    private Membership membership;
}
