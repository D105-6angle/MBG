package com.ssafy.controller.mission;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class MissionResponse {
    private Long missionId;
    private String type;
    private double latitude;
    private double longitude;
    private String regionName;
    private String storyContent;
    private String heritageContent;
    private String heritageAnswer;
}
