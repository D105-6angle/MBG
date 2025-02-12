package com.ssafy.controller.mission;


import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class MissionResponse {

    @Getter
    @Builder
    public static class MissionInfo {
        private Long missionId;
        private String positionName;
        private String codeId;
        private Double[] centerPoint;
        private List<Double[]> edgePoints;
        private boolean isCorrect;
    }
}
