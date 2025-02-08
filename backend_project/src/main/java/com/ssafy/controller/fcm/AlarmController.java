package com.ssafy.controller.fcm;

import java.util.List;

import com.ssafy.model.entity.Alarm;
import com.ssafy.model.service.fcm.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/alarm")
@CrossOrigin("*")
@Tag(name = "Alarm Controller", description = "알람 관리를 위한 API")
public class AlarmController {

    @Autowired
    private AlarmService alarmService;

    @Operation(summary = "특정 사용자 ID로 알람 목록을 조회한다.")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getAlarmsByUserId(@PathVariable Long userId) {
        List<Alarm> alarms = alarmService.getAlarmsByUserId(userId);
        return ResponseEntity.ok(alarms);
    }
}