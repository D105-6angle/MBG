package com.ssafy.model.mapper.mission.heritage;

import com.ssafy.model.entity.HeritageProblem;
import com.ssafy.model.entity.Log;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.Optional;

@Mapper
public interface HeritageMissionMapper {

    Optional<HeritageProblem> findByMissionId(@Param("missionId") Long missionId);

    int insertHeritageBook(@Param("userId") Long userId, @Param("cardId") Long cardId);

    int insertLog(@Param("userId") Long userId, @Param("cardId") Long cardId, @Param("result") boolean result);


    Log findByLog(@Param("userId") Long userId, @Param("cardId") Long cardId);

}
