package com.ssafy.controller.mission;


import com.ssafy.model.service.mission.MissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mission")
@RequiredArgsConstructor
@Tag(name = "미션 지역과 관련된 컨트롤러")
public class MissionController {
    private final MissionService missionService;

    // 안 쓰는 API 확률 높음
//    @Operation(summary = "관리자가 특정 방의 전체 미션 조회 API", description = "안 쓰는 API일 확률이 높다고 하심")
//    @GetMapping("/{roomId}/missions")
//    public ResponseEntity<?> getRoomMissions(@PathVariable Long roomId) {
//        try {
//            List<MissionResponse> missions = missionService.getMissionsByRoomId(roomId);
//            return ResponseEntity.ok(Map.of("missions", missions));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of(
//                            "message", "Failed to fetch missions",
//                            "error", e.getMessage()
//                    ));
//        }
//    }

    @Operation(summary = "특정 문화유산장소명에 해당하는 모든 미션 정보 가져오기")
    @PostMapping("/pickers")
    public ResponseEntity<?> getMissionInfoByPlace(@RequestBody MissionRequest.MissionsByHeritagePlace request) {
        List<MissionResponse.MissionInfo> response = missionService.getMisionInfoByPlace(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "특정 문화유산장소명에 해당하는 모든 미션 정보 상태만 주기적으로 가져오기")
    @PostMapping("/states")
    public ResponseEntity<?> getMissionStatesByPlace(@RequestBody MissionRequest.MissionsByHeritagePlace request) {
        List<MissionResponse.MissionState> response = missionService.getMissionStatesByPlace(request);
        return ResponseEntity.ok(response);
    }
}
