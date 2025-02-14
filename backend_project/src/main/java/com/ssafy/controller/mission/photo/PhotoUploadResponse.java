package com.ssafy.controller.mission.photo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoUploadResponse {
    private Long pictureId;
    private Long roomId;
    private Long userId;
    private Long missionId;
    private String pictureUrl;
    private String completionTime;
}