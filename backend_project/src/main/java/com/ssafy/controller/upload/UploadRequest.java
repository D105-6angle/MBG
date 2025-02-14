package com.ssafy.controller.upload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

public class UploadRequest {
    @Getter
    @Setter
    public static class Card {
        private String cardName;
        private MultipartFile cardImageFile;
    }


    @Getter
    @Setter
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
        private MultipartFile problemImage;
        private MultipartFile objectImage;
    }

    @Getter
    @Setter
    public static class StoryProblem {
        private String obtainableCardName;
        private String objectName;
        private String description;
        private String content;
        private MultipartFile blackIconImageFile;
        private MultipartFile colorIconImageFile;
    }

    @Getter
    @Setter
    public static class StoryQuiz {
        private String cardName;
        private String content;
        private String initial;
        private String answer;
    }
}