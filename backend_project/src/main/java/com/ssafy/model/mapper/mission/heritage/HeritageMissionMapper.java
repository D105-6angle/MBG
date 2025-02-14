package com.ssafy.model.mapper.mission.heritage;

import com.ssafy.model.entity.HeritageProblem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.Optional;

@Mapper
public interface HeritageMissionMapper {

    Optional<HeritageProblem> findByMissionId(@Param("missionId") Long missionId);

    int insertHeritageBook(@Param("userId") Long userId, @Param("cardId") Long cardId);

    int insertLog(@Param("userId") Long userId, @Param("cardId") Long cardId, @Param("result") boolean result);

    // 이미 정답인 로그 존재 확인
    int existsCorrectLog(@Param("userId") Long userId, @Param("cardId") Long cardId);

    // 마지막 오답 제출 시간 조회
    Date getLastIncorrectLogTime(@Param("userId") Long userId, @Param("cardId") Long cardId);

}
