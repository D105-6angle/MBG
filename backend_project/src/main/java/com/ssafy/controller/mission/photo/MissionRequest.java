package com.ssafy.controller.mission.photo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

public class MissionRequest {
    @Getter
    @Setter
    public static class PhotoUpload {
        private Long roomId;
        private Long missionId;
        private int groupNo;
        private MultipartFile photo;
    }
}