package com.ssafy.model.entity;

import com.ssafy.model.entity.Membership;
import com.ssafy.model.entity.Room;
import com.ssafy.model.entity.MissionPosition;
import com.ssafy.model.entity.User;
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
