package com.ssafy.controller.heritagebook;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class HeritagebookRequest {
    private Long cardId;
    private String name;
    private String imageUrl;
    private LocalDateTime collectedAt;
    private String codeId; // 카드 타입 (문화재 / 일화 카드)
}
