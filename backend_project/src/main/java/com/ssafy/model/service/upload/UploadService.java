package com.ssafy.model.service.upload;

import com.ssafy.controller.upload.UploadRequest;
import com.ssafy.exception.common.DatabaseOperationException;
import com.ssafy.exception.mypage.NotFoundCardException;
import com.ssafy.exception.mypage.NotFoundProblemException;
import com.ssafy.model.entity.Card;
import com.ssafy.model.entity.HeritageProblem;
import com.ssafy.model.entity.QuizType;
import com.ssafy.model.entity.StoryProblem;
import com.ssafy.model.mapper.upload.UploadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UploadService {
    private final UploadMapper uploadMapper;

    @Transactional
    public void createHeritageProblem(UploadRequest.HeritageProblem dto, String problemImageS3Key, String objectImageS3Key) throws DatabaseOperationException, NotFoundCardException {
        Long cardId = findCardIdbyName(dto.getObtainableCardName());
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
    public void createStoryProblem(UploadRequest.StoryProblem dto, String blackImageUrl, String colorImageUrl) throws DatabaseOperationException, NotFoundCardException {
        Long cardId = findCardIdbyName(dto.getObtainableCardName());
        StoryProblem storyProblem = StoryProblem.builder().cardId(cardId).objectName(dto.getObjectName()).description(dto.getDescription())
                .content(dto.getContent()).blackIconImageUrl(blackImageUrl).colorIconImageUrl(colorImageUrl).build();

        int result = uploadMapper.insertStoryProblem(storyProblem);
        if (result == 0) {
            throw new DatabaseOperationException("일화 문제 데이터 삽입 중 이상이 발생했습니다.");
        }
    }

    @Transactional
    public void createStoryQuiz(UploadRequest.StoryQuiz requestDto) throws NotFoundProblemException, NotFoundCardException, DatabaseOperationException{
        String cardName = requestDto.getCardName();
        // 1. 카드 테이블에서 해당 카드에 대한 번호 알아내기
        Long cardId = findCardIdbyName(cardName);
        // 2. 일화 문제 테이블에서 해당 카드에 대한 문제 번호 알아내기
        Long problemId = findProblemIdByCardId(cardId);

        QuizType quiz = QuizType.builder().problemId(problemId).content(requestDto.getContent()).initial(requestDto.getInitial())
                        .answer(requestDto.getAnswer()).build();
        int result = uploadMapper.insertStoryQuiz(quiz);
        if (result == 0) {
            throw new DatabaseOperationException("일화 퀴즈 삽입 중 이상이 발생했습니다.");
        }
    }

    @Transactional
    public void createHeritageCard(String cardName, String imageUrl) throws DatabaseOperationException {
        Card card = Card.builder().codeId("M001").name(cardName).imageUrl(imageUrl).build();
        int result = uploadMapper.insertCard(card);
        if (result == 0) {
            throw new DatabaseOperationException("문화재 카드 데이터 삽입 중 이상이 발생했습니다.");
        }
    }

    @Transactional
    public void createStoryCard(String cardName, String imageUrl) throws DatabaseOperationException {
        Card card = Card.builder().codeId("M002").name(cardName).imageUrl(imageUrl).build();
        int result = uploadMapper.insertCard(card);
        if (result == 0) {
            throw  new DatabaseOperationException("일화 카드 데이터 삽입 중 이상이 발생했습니다.");
        }
    }

    private Long findCardIdbyName(String cardName) throws NotFoundCardException {
        Long cardId = uploadMapper.findCardIdByName(cardName);
        if (cardId == null) {
            throw new NotFoundCardException("입력한 카드명에 대한 카드를 찾을 수 없습니다.");
        }

        return cardId;
    }

    private Long findProblemIdByCardId(Long cardId) throws NotFoundProblemException {
        Long problemId = uploadMapper.findProblemIdByCardId(cardId);
        if (problemId == null) {
            throw new NotFoundProblemException("해당 일화 카드에 대한 원본 문제를 찾을 수 없습니다.");
        }

        return problemId;
    }
}
