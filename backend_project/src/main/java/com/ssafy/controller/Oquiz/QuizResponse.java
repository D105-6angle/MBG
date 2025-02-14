package com.ssafy.controller.Oquiz;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {
    private String quizId;
    private String content;
    private String initial;
    private String blackIconUrl;
}