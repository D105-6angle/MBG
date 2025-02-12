package com.ssafy.controller.mission.heritage;

import com.ssafy.model.service.mission.heritage.HeritageMissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/missions/quiz/heritage")
@Tag(name = "문화재 문제", description = "객관식 문제 API")
@RequiredArgsConstructor
public class HeritageMissionController {

    private final HeritageMissionService heritageMissionService;

    @GetMapping("/{missionId}")
    @Operation(summary = "문화재 문제 전체 조회")
    public ResponseEntity<HeritageMissionResponse> getHeritageMission(@PathVariable Long missionId) {
        HeritageMissionResponse quiz = heritageMissionService.getHeritageMissionByMissionId(missionId);
        return ResponseEntity.ok(quiz);
    }


}
