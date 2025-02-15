package com.ssafy.model.service.fcm;

import com.ssafy.model.entity.Alarm;
import com.ssafy.model.entity.Schedule;
import com.ssafy.model.mapper.fcm.AlarmMapper;
import com.ssafy.model.mapper.schedule.ScheduleMapper;
import com.ssafy.model.service.FirebaseCloudMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleNotificationService {

    private final ScheduleMapper scheduleMapper;
    private final FcmService fcmService;
    private final FirebaseCloudMessageService firebaseCloudMessageService;
    private final AlarmMapper alarmMapper;  // AlarmMapper 주입 추가

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void checkUpcomingSchedules() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenMinutesLater = now.plusMinutes(10);

        log.info("Checking schedules between {} and {}", now, tenMinutesLater);

        // 현재 시간으로부터 10분 후에 있는 일정들을 조회
        List<Schedule> upcomingSchedules = scheduleMapper.findUpcomingSchedules(now, tenMinutesLater);
        log.info("Found {} upcoming schedules", upcomingSchedules.size());

        for (Schedule schedule : upcomingSchedules) {
            sendScheduleNotification(schedule);
        }
    }

    private void sendScheduleNotification(Schedule schedule) {
        try {
            // 해당 방의 모든 사용자 토큰 조회
            List<String> tokens = fcmService.getTokensByRoomId(schedule.getRoomId());
            List<Long> memberIds = fcmService.getStudentIdsByRoomId(schedule.getRoomId());

            String title = "일정 알림";
            String body = String.format("%s 일정이 10분 후에 시작됩니다.", schedule.getContent());

            // 각 토큰에 대해 알림 전송
            for (String token : tokens) {
                try {
                    firebaseCloudMessageService.sendMessageTo(token, title, body);
                    log.info("Successfully sent notification to token: {}", token);
                } catch (Exception e) {
                    log.error("Failed to send notification to token: {}", token, e);
                }
            }

            // 각 멤버별로 알람 저장
            for (Long userId : memberIds) {
                Alarm alarm = Alarm.builder()
                        .userId(userId)  // userId 필수
                        .title(title)
                        .content(body)
                        .build();

                alarmMapper.insert(alarm);  // insertAlarm 메서드명 수정
                log.info("Saved alarm for user {}: alarmId={}", userId, alarm.getAlarmId());
            }
        } catch (Exception e) {
            log.error("Error sending schedule notification for schedule {}", schedule.getScheduleId(), e);
        }
    }
}