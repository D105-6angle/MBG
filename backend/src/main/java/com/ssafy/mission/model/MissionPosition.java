package com.ssafy.mission.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MissionPosition {
    private Long missionId;
    private Long regionId;
    private String codeId;
    private Double latitude;
    private Double longitude;
}
