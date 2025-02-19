package com.ssafy.model.service.Oquiz;

import com.ssafy.controller.Oquiz.QuizInfo;
import com.ssafy.controller.Oquiz.QuizResponse;
import com.ssafy.controller.Oquiz.QuizResultResponse;
import com.ssafy.exception.Oquiz.QuizNotFoundException;
import com.ssafy.model.mapper.Oquiz.OquizMapper;

import com.ssafy.model.service.amazons3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService {
    private final OquizMapper oquizMapper;
    private final S3Service s3Service;

    public QuizResponse getRandomQuizByMissionId(Long missionId) {
        log.info("Fetching quiz for mission ID: {}", missionId);
        Optional<QuizResponse> quizOptional = Optional.ofNullable(oquizMapper.getRandomQuizByMissionId(missionId));

        QuizResponse quiz = quizOptional.orElseThrow(() -> new QuizNotFoundException(missionId));
        log.info("Quiz response: {}", quiz);

        return quiz;
    }

    @Transactional
    public QuizResultResponse processQuizAnswer(Long userId, Long missionId, Long quizId, String userAnswer) {
        // 1. 퀴즈 정보 조회
        QuizInfo quizInfo = oquizMapper.getQuizInfoByQuizId(quizId);
        if (quizInfo == null) {
            throw new RuntimeException("Invalid quiz_id: " + quizId);
        }

        // 2. 정답 체크
        boolean isCorrect = userAnswer.equals(quizInfo.getAnswer());
        log.warn("퀴즈 제출한 답: {}, 정답: {}, 정답 여부: {}", userAnswer, quizInfo.getAnswer(), isCorrect);

        // 3. 로그 저장
        oquizMapper.insertQuizLog(userId, quizInfo.getCardId(), isCorrect);

        // 4. 정답인 경우 꾸미백과에 추가
        if (isCorrect) {
            if (oquizMapper.checkHeritageBookExists(userId, quizInfo.getCardId()) == 0) {
                oquizMapper.insertHeritageBook(userId, quizInfo.getCardId());
            }
        }

        // 5. 카드 이미지 URL 가져오기
        String cardImageUrl_temp = oquizMapper.getCardImageUrlByCardId(quizInfo.getCardId());
        String presignedUrl = s3Service.generatePresignedUrl(cardImageUrl_temp);


        // 6. 결과 반환
        return QuizResultResponse.builder()
                .result(isCorrect)
                .cardImageUrl(presignedUrl)
                .build();
    }


}
