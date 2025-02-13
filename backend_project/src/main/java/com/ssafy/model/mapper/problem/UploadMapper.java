package com.ssafy.model.mapper.problem;

import com.ssafy.model.entity.Card;
import com.ssafy.model.entity.HeritageProblem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UploadMapper {
    int insertHeritageProblem(HeritageProblem dto);
    int insertCard(Card card);

    Long findCardIdByName(@Param("cardName") String cardName);
}
