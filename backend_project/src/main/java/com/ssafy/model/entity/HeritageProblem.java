package com.ssafy.model.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HeritageProblem {
    private Long problemId;
    private Long cardId;
    private String heritageName;
    private String imageUrl;
    private String description;
    private String objectImageUrl;
    private String content;
    private Double latitude;
    private Double longitude;
    private String example1;
    private String example2;
    private String example3;
    private String example4;
    private String answer;
}
