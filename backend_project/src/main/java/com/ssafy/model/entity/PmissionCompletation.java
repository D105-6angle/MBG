package com.ssafy.model.entity;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PmissionCompletation {
    private Long missionId;
    private Long userId;
    private LocalDateTime completionAt;

    // 연관
    private MissionPosition mission;
    private User user;
}
