package com.ssafy.model.service.heritagebook;

import com.ssafy.controller.heritagebook.HeritagebookResponse;
import com.ssafy.model.entity.HeritageBook;
import com.ssafy.model.mapper.heritagebook.HeritagebookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HeritagebookService {

    private final HeritagebookMapper heritagebookMapper;



    public HeritagebookResponse.ListResponse getAllCards(Long userId) {
        List<HeritageBook> cards = heritagebookMapper.findAllByUserId(userId);

        // 문화재별, 일화별 카드 조회
        int totalHeritageCards = heritagebookMapper.countCardsByType("M001");
        int totalStoryCards = heritagebookMapper.countCardsByType("M002");

        return HeritagebookResponse.ListResponse.builder()
                .totalCards(cards.size())
                .totalHeritageCards(totalHeritageCards)
                .totalStoryCards(totalStoryCards)
                .cards(cards.stream().map(this::toResponse).collect(Collectors.toList()))
                .build();
    }


    private HeritagebookResponse.DetailResponse toResponse(HeritageBook card) {
        return HeritagebookResponse.DetailResponse.builder()
                .cardId(card.getCardId())
                .cardName(card.getCard().getName())
                .imageUrl(card.getCard().getImageUrl())
                .collectedAt(card.getCreatedAt())
                .codeId(card.getCodeId())
                .build();
    }



}
