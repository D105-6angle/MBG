package com.ssafy.model.mapper.Oquiz;

import com.ssafy.controller.Oquiz.QuizInfo;
import com.ssafy.controller.Oquiz.QuizResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface OquizMapper {
    QuizResponse getRandomQuizByMissionId(Long missionId);

    QuizInfo getQuizInfoByMissionId(Long missionId);

    // 로그 저장
    void insertQuizLog(@Param("userId") Long userId,
                       @Param("cardId") Long cardId,
                       @Param("result") boolean result);

    // 꾸미백과 중복 체크
    int checkHeritageBookExists(@Param("userId") Long userId,
                                @Param("cardId") Long cardId);

    // 꾸미백과 저장
    void insertHeritageBook(@Param("userId") Long userId,
                            @Param("cardId") Long cardId);


    QuizInfo getQuizInfoByQuizId(@Param("quizId") Long quizId);


    String getCardImageUrlByCardId(@Param("cardId") Long cardId);


}
