package com.ssafy.controller.fcm;

import com.google.firebase.FirebaseApp;
import com.ssafy.model.service.fcm.FCMServiceTest;
import com.ssafy.model.service.fcm.FcmService;
import com.ssafy.model.service.fcm.TeacherNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fcm")
@CrossOrigin("*")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "FCM", description = "Firebase Cloud Messaging API")
public class FcmApiController {

    private final FcmService fcmService;
    private final FCMServiceTest fcmServiceTest;
    private final TeacherNoticeService teacherNoticeService;

//    private AlarmService alarmService;
//    private FirebaseCloudMessageService firebaseCloudMessageService;

    @Operation(summary = "FCM 설정 테스트")
    @GetMapping("/test")
    public ResponseEntity<String> testFCM() {
        try {
            FirebaseApp firebaseApp = FirebaseApp.getInstance();
            log.info("Firebase 앱 이름: {}", firebaseApp.getName());
            return ResponseEntity.ok("Firebase 설정이 정상적으로 완료되었습니다.");
        } catch (IllegalStateException e) {
            log.error("Firebase 초기화 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Firebase 설정에 문제가 있습니다: " + e.getMessage());
        }
    }

    @Operation(summary = "FCM 토큰을 추가한다.")
    @PostMapping("/add")
    public ResponseEntity<?> addToken(@RequestParam Long userId, @RequestParam String fcmToken) {
        int result = fcmService.addToken(userId, fcmToken);
        if (result > 0) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(500).body(false);
        }
    }

    //TODO - 선생님이 글을 작성하면 teacher_notice 등록되어야함과 동시에 해당 방에 있는 모든 학생들에게 푸시 알림이 가야한다.

    @Operation(summary = "교사 공지사항 등록 및 알림 전송")
    @PostMapping("/notice")
    public ResponseEntity<?> createNoticeAndNotify(
            @RequestParam Long roomId,
            @RequestParam Long teacherId,
            @RequestParam String title,
            @RequestParam String content) {
        try {
            teacherNoticeService.createNoticeAndNotify(roomId, teacherId, title, content);
            return ResponseEntity.ok("교사 공지사항 등록 및 알림 전송 완료");
        } catch (Exception e) {
            log.error("교사 공지사항 등록 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("교사 공지사항 등록 실패: " + e.getMessage());
        }
    }
}
