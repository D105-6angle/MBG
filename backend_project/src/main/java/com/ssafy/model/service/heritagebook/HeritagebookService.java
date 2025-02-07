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
        return HeritagebookResponse.ListResponse.builder()
                .totalCards(cards.size())
                .cards(cards.stream().map(this::toResponse).collect(Collectors.toList()))
                .build();
    }

    public HeritagebookResponse.DetailResponse getCardDetails(Long userId, Long cardId) {
        HeritageBook card = heritagebookMapper.findByUserIdAndCardId(userId, cardId);
        if (card == null) {
            throw new IllegalArgumentException("해당 카드를 찾을 수 없습니다. userId: " + userId + ", cardId: " + cardId);
        }
        return toResponse(card);
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
