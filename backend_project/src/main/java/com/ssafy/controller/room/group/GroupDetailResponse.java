package com.ssafy.controller.room.group;

import lombok.*;

import java.util.List;

// 특정 조 상세 정보
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupDetailResponse {

    private int groupNo;
    private double progress;
    private List<MemberDto> members;
    private List<VerificationPhotoDto> verificationPhotos;
    private List<VisitedPlaceDto> visitedPlaces;

    // 조원 정보
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberDto {
        private long userId;
        private String nickname;
        private String codeId;  // "J001"=팀장, "J002"=팀원
    }

    // 인증샷
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerificationPhotoDto {
        private long pictureId;
        private String pictureUrl;
        private long missionId;
        private String completionTime; // "2025-02-12 10:00:00"
    }

    // 방문한 장소
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VisitedPlaceDto {
        private long missionId;
        private String positionName;
        private String completedAt; // "yyyy-MM-dd HH:mm:ss"
    }

}