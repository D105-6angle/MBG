package com.ssafy.controller.fcm;

import com.google.firebase.FirebaseApp;
import com.ssafy.controller.notification.CreateNoticeRequest;
import com.ssafy.controller.notification.NoticeDto;
import com.ssafy.model.service.fcm.FCMServiceTest;
import com.ssafy.model.service.fcm.FcmService;
import com.ssafy.model.service.notification.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/fcm")
@CrossOrigin("*")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "FCM", description = "Firebase Cloud Messaging API")
public class FcmApiController {

    private final FcmService fcmService;
    private final FCMServiceTest fcmServiceTest;
    private NoticeService noticeService;
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

    //TODO - 선생님이 글을 작성하면 notifications에 등록되어야함과 동시에 해당 방에 있는 모든 학생들에게 푸시 알림이 가야한다.

//        @Operation(summary = "공지사항을 작성하고 해당 방의 모든 학생에게 FCM 알림을 전송합니다.")
//        @PostMapping("/notice")
//        public ResponseEntity<?> createNotice(@RequestBody CreateNoticeRequest request) {
//            NoticeDto notice = noticeService.createNoticeAndSendFCM(request);
//
//            if (notice.getStatus()) {
//                return ResponseEntity.ok("메시지 전송 성공");
//            } else {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .body("메시지 전송 실패");
//            }
//        }
    }
