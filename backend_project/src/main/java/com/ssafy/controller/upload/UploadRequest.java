package com.ssafy.controller.upload;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class UploadRequest {
    @Getter
    @NoArgsConstructor
    @ToString
    public static class HeritageProblem {
        private String obtainableCardName;
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