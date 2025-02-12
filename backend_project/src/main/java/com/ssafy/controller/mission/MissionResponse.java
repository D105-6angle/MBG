package com.ssafy.controller.mission;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MissionState {
        private Long missionId;
        private boolean isCorrect;
    }
}