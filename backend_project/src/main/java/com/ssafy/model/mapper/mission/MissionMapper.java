package com.ssafy.model.mapper.mission;

import com.ssafy.controller.mission.MissionResponse;
import com.ssafy.model.entity.MissionPosition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MissionMapper {
    // Room 쪽으로 옮기기
    List<MissionResponse> getMissionsByRoomId(Long roomId);
    List<MissionPosition> getMissionInfoByPlace(@Param("userId") Long userId, @Param("roomId") Long roomId, @Param("placeName") String placeName);
}
