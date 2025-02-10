package com.ssafy.model.service.fcm;

import com.ssafy.controller.TeacherNotice.TeacherNoticeDto;
import com.ssafy.model.entity.Alarm;
import com.ssafy.model.entity.TeacherNotice;
import com.ssafy.model.mapper.dao.FcmDao;
import com.ssafy.model.mapper.fcm.TeacherNoticeMapper;
import com.ssafy.model.service.FirebaseCloudMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TeacherNoticeService {
    private final TeacherNoticeMapper teacherNoticeMapper;
    private final FcmService fcmService;
    private final FcmDao fcmDao;
    private final AlarmService alarmService;
    private final FirebaseCloudMessageService firebaseCloudMessageService;

    @Transactional
    public void createNoticeAndNotify(Long roomId, String content) {
        String title = "공지 알림";
        TeacherNotice notice = TeacherNotice.builder()
                .roomId(roomId)
                .title(title)
                .content(content)
                .build();

        teacherNoticeMapper.insertNotice(notice);

        // 2. FCM 토큰 조회
        List<String> tokens = fcmService.getTokensByRoomId(roomId);

        // 3. 학생 ID 조회
        List<Long> studentIds = fcmService.getStudentIdsByRoomId(roomId);

        // 4. 알람 저장 및 FCM 메시지 전송
        boolean allSuccess = true;
        List<IOException> errors = new ArrayList<>();

        // FCM 메시지 전송
        for (String token : tokens) {
            try {
                firebaseCloudMessageService.sendMessageTo(token, title, content);
            } catch (IOException e) {
                allSuccess = false;
                errors.add(e);
                log.error("FCM 메시지 전송 실패 (token: {}): {}", token, e.getMessage());
            }
        }

        // FCM 전송이 성공한 경우에만 알람 저장 및 상태 업데이트
        if (allSuccess) {
            for (Long studentId : studentIds) {
                alarmService.addAlarm(new Alarm(studentId, title, content));
            }
            teacherNoticeMapper.updateNoticeStatus(notice.getNoticeId(), true);
        } else if (errors.size() == tokens.size()) {
            throw new RuntimeException("모든 FCM 메시지 전송이 실패했습니다.");
        }
    }

    public List<TeacherNoticeDto> getNoticesByRoomId(Long roomId) {
        return teacherNoticeMapper.getNoticesByRoomId(roomId);
    }

//    @Transactional
//    public void createNoticeAndNotify(Long roomId, Long teacherId, String title, String content) {
//        TeacherNotice notice = TeacherNotice.builder()
//                .roomId(roomId)
//                .title(title)
//                .content(content)
//                .build();
//
//        teacherNoticeMapper.insertNotice(notice);
//
//        // 2. FCM 토큰 조회
//        List<String> tokens = fcmService.getTokensByRoomId(roomId);
//
//        // 3. 학생 ID 조회
//        List<Long> studentIds = fcmService.getStudentIdsByRoomId(roomId);
//
//        // 4. 알람 저장 및 FCM 메시지 전송
//        boolean allSuccess = true;
//        List<IOException> errors = new ArrayList<>();
//
//        // FCM 메시지 전송
//        for (String token : tokens) {
//            try {
//                firebaseCloudMessageService.sendMessageTo(token, title, content);
//            } catch (IOException e) {
//                allSuccess = false;
//                errors.add(e);
//                log.error("FCM 메시지 전송 실패 (token: {}): {}", token, e.getMessage());
//            }
//        }
//
//        // FCM 전송이 성공한 경우에만 알람 저장 및 상태 업데이트
//        if (allSuccess) {
//            for (Long studentId : studentIds) {
//                alarmService.addAlarm(new Alarm(studentId, title, content));
//            }
//            teacherNoticeMapper.updateNoticeStatus(notice.getNoticeId(), true);
//        } else if (errors.size() == tokens.size()) {
//            throw new RuntimeException("모든 FCM 메시지 전송이 실패했습니다.");
//        }
//    }
}
