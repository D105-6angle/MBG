package com.ssafy.controller.mission;


import com.ssafy.model.service.mission.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class MissionController {
    private final MissionService MissionService;

    @GetMapping("/{roomId}/missions")
    public ResponseEntity<?> getRoomMissions(@PathVariable Long roomId) {
        try {
            List<MissionResponse> missions = MissionService.getMissionsByRoomId(roomId);
            return ResponseEntity.ok(Map.of("missions", missions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message", "Failed to fetch missions",
                            "error", e.getMessage()
                    ));
        }
    }
}
