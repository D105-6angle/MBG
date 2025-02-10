package com.ssafy.controller.TeacherNotice;

import com.ssafy.model.service.fcm.TeacherNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
@Tag(name = "공지 알림", description = "공지 알림 API")
public class TeacherNoticeController {
    private final TeacherNoticeService teacherNoticeService;
    @Operation(summary = "공지 전체 조회")
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<TeacherNoticeDto>> getNoticesByRoom(@PathVariable Long roomId) {
        List<TeacherNoticeDto> notices = teacherNoticeService.getNoticesByRoomId(roomId);
        return ResponseEntity.ok(notices);
    }
}
