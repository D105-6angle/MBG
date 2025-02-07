package com.ssafy.model.entity;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HeritageBook {
    private Long userId;
    private Long cardId;
    private LocalDateTime createdAt;
    private String codeId;

    // 연관
    private User user;
    private Card card;

}
