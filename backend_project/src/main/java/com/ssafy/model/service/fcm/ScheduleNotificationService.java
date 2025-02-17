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
    private final AlarmMapper alarmMapper;

    @Scheduled(fixedRate = 60000)
    public void checkUpcomingSchedules() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenMinutesLater = now.plusMinutes(10);

        // 현재 시간으로부터 9분~10분 사이에 있는 일정들만 조회하도록 수정
        LocalDateTime nineMinutesLater = now.plusMinutes(9);

        log.info("Checking schedules between {} and {}", nineMinutesLater, tenMinutesLater);

        // 9분~10분 사이의 일정만 조회하도록 수정
        List<Schedule> upcomingSchedules = scheduleMapper.findUpcomingSchedules(nineMinutesLater, tenMinutesLater);
        log.info("Found {} upcoming schedules", upcomingSchedules.size());

        for (Schedule schedule : upcomingSchedules) {
            sendScheduleNotification(schedule);
        }
    }

    private void sendScheduleNotification(Schedule schedule) {
        try {
            List<String> tokens = fcmService.getTokensByRoomId(schedule.getRoomId());
            List<Long> memberIds = fcmService.getStudentIdsByRoomId(schedule.getRoomId());

            String title = "일정 알림";
            String body = String.format("%s 일정이 10분 후에 시작됩니다.", schedule.getContent());

            for (String token : tokens) {
                try {
                    firebaseCloudMessageService.sendMessageTo(token, title, body);
                    log.info("Successfully sent notification to token: {}", token);
                } catch (Exception e) {
                    log.error("Failed to send notification to token: {}", token, e);
                }
            }

            for (Long userId : memberIds) {
                Alarm alarm = Alarm.builder()
                        .userId(userId)
                        .title(title)
                        .content(body)
                        .build();

                alarmMapper.insert(alarm);
                log.info("Saved alarm for user {}: alarmId={}", userId, alarm.getAlarmId());
            }
        } catch (Exception e) {
            log.error("Error sending schedule notification for schedule {}", schedule.getScheduleId(), e);
        }
    }
}