package com.ssafy.model.service.upload;

import com.ssafy.controller.upload.UploadRequest;
import com.ssafy.exception.common.DatabaseOperationException;
import com.ssafy.exception.mypage.NotFoundCardException;
import com.ssafy.model.entity.Card;
import com.ssafy.model.entity.HeritageProblem;
import com.ssafy.model.mapper.problem.UploadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UploadService {
    private final UploadMapper uploadMapper;

    @Transactional
    public void createHeritageProblem(UploadRequest.HeritageProblem dto, String problemImageS3Key, String objectImageS3Key) throws IOException,
            DatabaseOperationException, NotFoundCardException {
        Long cardId = uploadMapper.findCardIdByName(dto.getObtainableCardName());
        if (cardId == null) {
            throw new NotFoundCardException("입력한 카드명에 대한 카드를 찾을 수 없습니다.");
        }

        HeritageProblem heritageProblemsDto = com.ssafy.model.entity.HeritageProblem.builder()
                .cardId(cardId)
                .heritageName(dto.getHeritageName())
                .imageUrl(problemImageS3Key)
                .objectImageUrl(objectImageS3Key)
                .description(dto.getDescription())
                .content(dto.getContent())
                .example1(dto.getExample1())
                .example2(dto.getExample2())
                .example3(dto.getExample3())
                .example4(dto.getExample4())
                .answer(dto.getAnswer())
                .build();

        int result = uploadMapper.insertHeritageProblem(heritageProblemsDto);
        if (result == 0) {
            throw new DatabaseOperationException("문화재 문제 데이터 삽입 중 이상이 발생했습니다.");
        }
    }

    @Transactional
    public void createHeritageCard(String cardName, String imageUrl) throws IOException, DatabaseOperationException {
        Card card = Card.builder().codeId("M001").name(cardName).imageUrl(imageUrl).build();
        int result = uploadMapper.insertCard(card);
        if (result == 0) {
            throw new DatabaseOperationException("문화재 카드 데이터 삽입 중 이상이 발생했습니다.");
        }
    }

    @Transactional
    public void createStoryCard(String cardName, String imageUrl) throws IOException, DatabaseOperationException {
        Card card = Card.builder().codeId("M002").name(cardName).imageUrl(imageUrl).build();
        int result = uploadMapper.insertCard(card);
        if (result == 0) {
            throw  new DatabaseOperationException("일화 카드 데이터 삽입 중 이상이 발생했습니다.");
        }
    }
}
