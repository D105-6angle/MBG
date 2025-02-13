package com.ssafy.controller.problem;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class ProblemRequest {
    @Getter
    @NoArgsConstructor
    public static class HeritageRequest {
        private Long cardId;
        private String heritageName;
        private String description;
        private String content;
        private String example1;
        private String example2;
        private String example3;
        private String example4;
        private String answer;
    }
}
