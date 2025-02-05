package com.ssafy.mission.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizType {
    private Long quizId;
    private Long problemId;
    private String content;
    private String initial;
    private String answer;
}
