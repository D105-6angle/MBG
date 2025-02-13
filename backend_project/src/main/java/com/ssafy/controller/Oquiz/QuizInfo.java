package com.ssafy.controller.Oquiz;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizInfo {
    private Long quizId;
    private Long problemId;
    private String content;
    private String initial;
    private String answer;
    private Long cardId;
}
