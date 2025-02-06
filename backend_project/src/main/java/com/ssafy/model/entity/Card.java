package com.ssafy.model.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {
    private Long cardId;
    private Long regionId;
    private String codeId;
    private String name;
    private String imageUrl;
}
