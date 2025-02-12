package com.ssafy.model.mapper.mission;

import com.ssafy.controller.mission.MissionResponse;
import com.ssafy.model.entity.MissionPosition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MissionMapper {
    List<MissionResponse> getMissionsByRoomId(Long roomId);     // 안 쓰는 API 확률 높음
    List<MissionPosition> getMissionInfoByPlace(@Param("userId") Long userId, @Param("roomId") Long roomId, @Param("placeName") String placeName);

    List<MissionResponse.MissionState> getMissionStatesByPlace(@Param("userId") Long userId, @Param("roomId") Long roomId, @Param("placeName") String placeName);
}
