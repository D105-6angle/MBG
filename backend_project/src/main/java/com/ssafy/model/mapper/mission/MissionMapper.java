package com.ssafy.model.mapper.mission;

import com.ssafy.controller.mission.MissionResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MissionMapper {
    List<MissionResponse> getMissionsByRoomId(Long roomId);
}
