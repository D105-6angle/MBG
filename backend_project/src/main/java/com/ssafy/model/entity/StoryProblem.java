package com.ssafy.model.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryProblem {
    private Long problemId;
    private Long cardId;
    private String objectName;
    private String description;
    private String content;
    private String blackIconImageUrl;
    private String colorIconImageUrl;
}
