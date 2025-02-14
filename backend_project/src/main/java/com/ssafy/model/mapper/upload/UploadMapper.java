package com.ssafy.model.mapper.upload;

import com.ssafy.model.entity.Card;
import com.ssafy.model.entity.HeritageProblem;
import com.ssafy.model.entity.QuizType;
import com.ssafy.model.entity.StoryProblem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UploadMapper {
    int insertHeritageProblem(HeritageProblem dto);
    int insertStoryProblem(StoryProblem dto);
    int insertCard(Card card);
    int insertStoryQuiz(QuizType quiz);

    Long findCardIdByName(@Param("cardName") String cardName);
    Long findProblemIdByCardId(@Param("cardId") Long cardId);
}
