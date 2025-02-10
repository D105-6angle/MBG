package com.ssafy.model.service.fcm;

import com.ssafy.model.entity.Schedule;
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

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void checkUpcomingSchedules() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenMinutesLater = now.plusMinutes(10);

        // 현재 시간으로부터 10분 후에 있는 일정들을 조회
        List<Schedule> upcomingSchedules = scheduleMapper.findUpcomingSchedules(now, tenMinutesLater);

        for (Schedule schedule : upcomingSchedules) {
            sendScheduleNotification(schedule);
        }
    }

    private void sendScheduleNotification(Schedule schedule) {
        try {
            // 해당 방의 모든 사용자 토큰 조회
            List<String> tokens = fcmService.getTokensByRoomId(schedule.getRoomId());

            String title = "일정 알림";
            String body = String.format("%s 일정이 10분 후에 시작됩니다.", schedule.getContent());

            // 각 토큰에 대해 알림 전송
            for (String token : tokens) {
                try {
                    firebaseCloudMessageService.sendMessageTo(token, title, body);
                } catch (Exception e) {
                    log.error("Failed to send notification to token: " + token, e);
                }
            }
        } catch (Exception e) {
            log.error("Error sending schedule notification", e);
        }
    }
}