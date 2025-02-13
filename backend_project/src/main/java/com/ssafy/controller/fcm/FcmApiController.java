package com.ssafy.controller.fcm;

import com.google.firebase.FirebaseApp;
import com.ssafy.model.entity.Alarm;
import com.ssafy.model.service.FirebaseCloudMessageService;
import com.ssafy.model.service.fcm.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final ScheduleNotificationService scheduleNotificationService;
    private final AlarmService alarmService;


    //    private AlarmService alarmService;
    private final FirebaseCloudMessageService firebaseCloudMessageService;

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

    @Operation(summary = "FCM 토큰을 추가/업데이트한다.")
    @PostMapping("/add")
    public ResponseEntity<?> updateToken(@RequestParam Long userId, @RequestParam String fcmToken) {
        try {
            int result = fcmService.addToken(userId, fcmToken);
            return ResponseEntity.ok(result > 0);
        } catch (Exception e) {
            log.error("FCM 토큰 업데이트 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("FCM 토큰 업데이트 실패: " + e.getMessage());
        }
    }

    //TODO - 선생님이 글을 작성하면 teacher_notice 등록되어야함과 동시에 해당 방에 있는 모든 학생들에게 푸시 알림이 가야한다.

//    @Operation(summary = "교사 공지사항 등록 및 반 학생들에게 알림 전송")
//    @PostMapping("/notice")
//    public ResponseEntity<?> createNoticeAndNotify(
//            @RequestParam Long roomId,
////            @RequestParam Long teacherId,
////            @RequestParam String title,
//            @RequestParam String content) {
//        try {
////            teacherNoticeService.createNoticeAndNotify(roomId, teacherId, title, content);
////            teacherNoticeService.createNoticeAndNotify(roomId, teacherId, content);
//              teacherNoticeService.createNoticeAndNotify(roomId, content);
//            return ResponseEntity.ok("교사 공지사항 등록 및 알림 전송 완료");
//        } catch (Exception e) {
//            log.error("교사 공지사항 등록 실패: ", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("교사 공지사항 등록 실패: " + e.getMessage());
//        }
//    }

    @PostMapping("/notice")
    public ResponseEntity<?> createNoticeAndNotify(
            @RequestParam Long roomId,
            @RequestParam String content) {
        try {
            // 파라미터 값 로깅
            System.out.println("=== Notice Parameters ===");
            System.out.println("roomId: " + roomId);
            System.out.println("content: " + content);
            // 또는 로거 사용
            log.info("Notice Parameters - roomId: {}, content: {}", roomId, content);

            teacherNoticeService.createNoticeAndNotify(roomId, content);

            // 성공 로깅
            System.out.println("=== Notice Creation Success ===");
            return ResponseEntity.ok("교사 공지사항 등록 및 알림 전송 완료");
        } catch (Exception e) {
            // 실패 상세 로깅
            System.out.println("=== Notice Creation Failed ===");
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Error cause: " + e.getCause());
            e.printStackTrace(); // 스택 트레이스 전체 출력

            log.error("교사 공지사항 등록 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("교사 공지사항 등록 실패: " + e.getMessage());
        }
    }

    //TODO - 일정을 입력하면 해당 일정 종료 일정시간 이전에 푸시알림ex. 점심 시간 10분전에 10분뒤 점심시간이라고 푸시알림

    //TODO - 만족도 조사하기 버튼 클릭하면 해당 방에 있는 모든 학생에게 만족도 조사를 하라는 알림이 간다. (FCM의 room으로 만족도 조사페이지로 이동하도록)
    @Operation(summary = "만족도 조사 알림 전송")
    @PostMapping("/send-survey")
    public ResponseEntity<?> sendSurveyNotification(@RequestParam Long roomId) {
        try {
            List<String> tokens = fcmService.getTokensByRoomId(roomId);
            String title = "만족도 조사 알림";
            String body = "현장 체험 학습 만족도 조사를 진행해주세요.";
            List<Long> studentIds = fcmService.getStudentIdsByRoomId(roomId);

            for (String token : tokens) {
                try {
                    Map<String, String> data = new HashMap<>();
                    data.put("type", "SURVEY");
                    data.put("roomId", roomId.toString());
                    data.put("route", "SURVEY"); // 프론트엔드의 라우트 경로

                    // 데이터와 함께 알림 전송
                    firebaseCloudMessageService.sendMessageWithData(token, title, body, data);
                } catch (Exception e) {
                    log.error("Failed to send survey notification to token: " + token, e);
                }
            }
            for (Long studentId : studentIds) {
                alarmService.addAlarm(new Alarm(studentId, title, body));
            }
            return ResponseEntity.ok("만족도 조사 알림이 전송되었습니다.");
        } catch (Exception e) {
            log.error("만족도 조사 알림 전송 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("만족도 조사 알림 전송 실패: " + e.getMessage());
        }
    }
}
