package com.ssafy.model.service.mission;

import com.ssafy.controller.mission.MissionResponse;
import com.ssafy.model.mapper.mission.MissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class MissionService {
    private final MissionMapper missionMapper;

    public List<MissionResponse> getMissionsByRoomId(Long roomId){
        return missionMapper.getMissionsByRoomId(roomId);
    }
}
