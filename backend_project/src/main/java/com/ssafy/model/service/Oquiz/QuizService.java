package com.ssafy.model.service.Oquiz;

import com.ssafy.controller.Oquiz.QuizInfo;
import com.ssafy.controller.Oquiz.QuizResponse;
import com.ssafy.controller.Oquiz.QuizResultResponse;
import com.ssafy.exception.Oquiz.QuizNotFoundException;
import com.ssafy.model.mapper.Oquiz.OquizMapper;

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

    public QuizResponse getRandomQuizByMissionId(Long missionId) {
        log.info("Fetching quiz for mission ID: {}", missionId);
        Optional<QuizResponse> quizOptional = Optional.ofNullable(oquizMapper.getRandomQuizByMissionId(missionId));

        QuizResponse quiz = quizOptional.orElseThrow(() -> new QuizNotFoundException(missionId));
        log.info("Quiz response: {}", quiz);

        return quiz;
    }

    @Transactional
    public QuizResultResponse processQuizAnswer(Long userId, Long missionId, String userAnswer) {
        // 1. 퀴즈 정보 조회
        OquizMapper quizMapper;
        QuizInfo quizInfo = oquizMapper.getQuizInfoByMissionId(missionId);
        if (quizInfo == null) {
            throw new RuntimeException("Invalid mission_id: " + missionId);
        }

        // 2. 정답 체크 (초성 비교)
        boolean isCorrect = userAnswer.equals(quizInfo.getAnswer());
        // 3. 로그 저장
        oquizMapper.insertQuizLog(userId, quizInfo.getCardId(), isCorrect);

        // 4. 정답인 경우 꾸미백과에 추가
        if (isCorrect) {
            if (oquizMapper.checkHeritageBookExists(userId, quizInfo.getCardId()) == 0) {
                oquizMapper.insertHeritageBook(userId, quizInfo.getCardId());
            }
        }

        // 5. 결과 반환
        QuizResultResponse response = new QuizResultResponse();
        response.setResult(isCorrect);
        return response;
    }


}