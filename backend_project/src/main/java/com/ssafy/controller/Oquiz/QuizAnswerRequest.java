package com.ssafy.controller.Oquiz;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizAnswerRequest {
    private Long userId;
    private String answer;
    private Long quizId;
}
