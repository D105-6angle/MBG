package com.ssafy.controller.fcm;

import com.google.firebase.FirebaseApp;
import com.ssafy.model.service.fcm.FCMServiceTest;
import com.ssafy.model.service.fcm.FcmService;
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
}