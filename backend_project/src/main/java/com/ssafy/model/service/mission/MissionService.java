package com.ssafy.model.service.mission;

import com.ssafy.controller.mission.MissionRequest;
import com.ssafy.controller.mission.MissionResponse;
import com.ssafy.model.entity.MissionPosition;
import com.ssafy.model.mapper.mission.MissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionService {
    private final MissionMapper missionMapper;

    // Legacy: 안쓰는 API일 확률 높음
    public List<MissionResponse> getMissionsByRoomId(Long roomId){
        return missionMapper.getMissionsByRoomId(roomId);
    }

    public List<MissionResponse.MissionState> getMissionStatesByPlace(MissionRequest.MissionsByHeritagePlace request) {
        List<MissionResponse.MissionState> states = missionMapper.getMissionStatesByPlace(request.getUserId(), request.getRoomId(), request.getPlaceName());
        return states;
    }

    public List<MissionResponse.MissionInfo> getMisionInfoByPlace(MissionRequest.MissionsByHeritagePlace request) {
        List<MissionPosition> info = missionMapper.getMissionInfoByPlace(request.getUserId(), request.getRoomId(), request.getPlaceName());
        List<MissionResponse.MissionInfo> response = new ArrayList<>();
        
        // 좌표 데이터들 파싱해서 응답 데이터 목록 만들기
        for (MissionPosition i : info) {
            MissionResponse.MissionInfo mission = MissionResponse.MissionInfo.builder().missionId(i.getMissionId())
                    .positionName(i.getPositionName()).centerPoint(paresPoint(i.getCenterPoint()))
                    .edgePoints(parsePolygon(i.getEdgePoints())).codeId(i.getCodeId()).isCorrect(i.isCorrect()).build();
            response.add(mission);
        }

        return response;
    }

    // 중앙점 parsing
    private Double[] paresPoint(String pointStr) {
        if (pointStr == null)  return null;

        // "POINT(경도 위도)" 형식 파싱
        String coords = pointStr.replace("POINT(", "").replace(")", "");
        String[] coordinates = coords.split(" ");
        return new Double[] {Double.parseDouble(coordinates[1]), Double.parseDouble(coordinates[0])};
    }

    // 가장자리 점들 parsing
    private List<Double[]> parsePolygon(String polygonStr) {
        if (polygonStr == null) return null;

        // "POLYGON((경도1 위도1,경도2 위도2,...))" 형식 파싱
        String coords = polygonStr.replace("POLYGON((", "").replace("))", "");
        List<Double[]> points = Arrays.stream(coords.split(","))
                .map(String::trim)
                .map(pointStr -> {
                    String[] coordinates = pointStr.split(" ");
                    return new Double[] {Double.parseDouble(coordinates[1]), Double.parseDouble(coordinates[0])};
                }).collect(Collectors.toList());

        // 마지막 좌표는 맨 처음과 같아서 제외
        if (!points.isEmpty()) {
            points.remove(points.size() - 1);
        }

        return points;
    }
}
