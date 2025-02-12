package com.ssafy.model.mapper.mission.heritage;

import com.ssafy.model.entity.HeritageProblem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface HeritageMissionMapper {

    Optional<HeritageProblem> findByMissionId(@Param("missionId") Long missionId);

}
