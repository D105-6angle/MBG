package com.ssafy.controller.Oquiz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {
    private String content;
    private String inital;
    private String blackIconUrl;
}