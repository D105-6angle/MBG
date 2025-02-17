package com.ssafy.controller.heritagebook;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class HeritagebookResponse {

    @Getter
    @Builder
    public static class ListResponse {
        private int totalCards;
        private int totalStoryCards;
        private int totalHeritageCards;
        private List<DetailResponse> cards;
    }

    @Getter
    @Setter
    @Builder
    public static class DetailResponse {
        private Long cardId;
        private String cardName;
        private String imageUrl;
        private LocalDateTime collectedAt;
        private String codeId;
    }

    @Getter
    @Builder
    public static class ErrorDto {
        private int status;
        private String message;
        private String error;
    }



}
