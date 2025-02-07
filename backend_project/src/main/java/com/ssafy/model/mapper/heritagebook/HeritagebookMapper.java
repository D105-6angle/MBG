package com.ssafy.model.mapper.heritagebook;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ssafy.model.entity.HeritageBook;

import java.util.List;

@Mapper
public interface HeritagebookMapper {

    // 전체 도감 조회
    List<HeritageBook> findAllByUserId(@Param("userId") Long userId);

    // 특정 카드 조회
    HeritageBook findByUserIdAndCardId(@Param("userId") Long userId, @Param("cardId") Long cardId);

}
