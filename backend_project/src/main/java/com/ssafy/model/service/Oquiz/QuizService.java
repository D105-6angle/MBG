package com.ssafy.model.service.Oquiz;

import com.ssafy.controller.Oquiz.QuizResponse;
import com.ssafy.exception.Oquiz.QuizNotFoundException;
import com.ssafy.model.mapper.Oquiz.OquizMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
}