package com.ssafy.controller.mypage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

public class MyPageResponse {
    // 마이페이지 정보
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MyPageInfo {
        private UserInfo userInfo;
        private List<AttemptedProblem> attemptedProblems;
    }

    // 마이페이지 유저 정보
    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserInfo {
        private String email;
        private String name;
        private String nickname;
        @JsonProperty("isFinishedReport")
        private boolean isFinishedReport;
    }

    // 시도한 문제
    @Getter
    @Setter
    @NoArgsConstructor
    public static class AttemptedProblem {
        private Long cardId;
        private String name;
        private String imageUrl;
        private String lastAttempedAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class LogDetail {
        private String cardName;
        private String imageUrl;
        private String description;
        private String lastAttemptedAt;
    }
}