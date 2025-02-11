package com.ssafy.controller.mission;


import com.ssafy.model.service.mission.MissionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mission")
@RequiredArgsConstructor
public class MissionController {
    private final MissionService missionService;

    // TODO: Room 쪽으로 옮기자 좀 수정해서
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
    @GetMapping("/pickers")
    public ResponseEntity<?> getMissionInfoByPlace(@RequestBody MissionRequest.MissionsByHeritagePlace request) {
        List<MissionResponse.MissionInfo> response = missionService.getMisionInfoByPlace(request);
        return ResponseEntity.ok(response);
    }
}
