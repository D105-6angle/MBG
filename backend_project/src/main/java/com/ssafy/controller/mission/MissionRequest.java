package com.ssafy.controller.mission;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class MissionRequest {

    @Getter
    @NoArgsConstructor
    public static class MissionsByHeritagePlace {
        private Long userId;
        private Long roomId;
        private String placeName;
    }
}
