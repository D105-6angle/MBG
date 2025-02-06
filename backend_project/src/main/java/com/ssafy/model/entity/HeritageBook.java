package com.ssafy.model.entity;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HeritageBook {
    private Long userId;
    private Long cardId;
    private LocalDate createdAt;

    // 연관
    private User user;
    private Card card;
}
