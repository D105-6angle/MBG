package com.ssafy.model.mapper.Oquiz;

import com.ssafy.controller.Oquiz.QuizResponse;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface OquizMapper {
    QuizResponse getRandomQuizByMissionId(Long missionId);
}
