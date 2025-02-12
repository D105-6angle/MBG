package com.ssafy.model.service.Oquiz;

import com.ssafy.controller.Oquiz.QuizResponse;
import com.ssafy.model.mapper.Oquiz.OquizMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final OquizMapper oquizMapper;

    public QuizResponse getRandomQuizByMissionId(Long missionId) {
        QuizResponse quiz = oquizMapper.getRandomQuizByMissionId(missionId);
        if (quiz == null) {
            throw new NotFoundException("해당 미션에 대한 퀴즈 없음");
        }
        return quiz;
    }
}