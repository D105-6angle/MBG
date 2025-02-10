package com.ssafy.controller.room.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 특정 조 상세 정보
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupDetailResponse {
    private int groupNo;
    private double progress;                // (완료 미션 수 / 전체 미션 수)
    private List<MemberDto> members;        // 조에 속한 멤버 목록
    private List<VisitedPlace> visitedPlaces;  // 조가 방문한 장소 목록

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberDto {
        private long userId;
        private String name;
        private String nickname;
        private String email;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VisitedPlace {
        private long missionId;
        private String missionTitle;
        private String completedAt; // "yyyy-MM-dd HH:mm:ss"
    }
}