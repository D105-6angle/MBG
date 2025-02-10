package com.ssafy.model.service.schedule;

import com.ssafy.controller.schedule.ScheduleRequest;
import com.ssafy.controller.schedule.ScheduleResponse;
import com.ssafy.model.mapper.schedule.ScheduleMapper;
import com.ssafy.model.entity.Schedule;
import com.ssafy.model.service.fcm.ScheduleNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleMapper scheduleMapper;
    private final ScheduleNotificationService notificationService;

    public ScheduleResponse.ListResponse getSchedulesByRoomId(Long roomId) {
        List<Schedule> schedules = scheduleMapper.findByRoomId(roomId);
        List<ScheduleResponse.Schedule> responses = schedules.stream()
                .map(this::toScheduleResponse)
                .collect(Collectors.toList());

        return ScheduleResponse.ListResponse.builder()
                .schedules(responses)
                .build();
    }

    public ScheduleResponse.Schedule getSchedule(Long roomId, Long scheduleId) {
        Schedule schedule = scheduleMapper.findByRoomIdAndScheduleId(roomId, scheduleId);
        return toScheduleResponse(schedule);
    }

    public ScheduleResponse.Schedule createSchedule(Long roomId, ScheduleRequest request) {
        Schedule schedule = Schedule.builder()
                .roomId(roomId)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .content(request.getContent())
                .build();

        scheduleMapper.insert(schedule);
        return toScheduleResponse(schedule);
    }

    public ScheduleResponse.Schedule updateSchedule(Long roomId, Long scheduleId, ScheduleRequest request) {
        Schedule schedule = Schedule.builder()
                .scheduleId(scheduleId)
                .roomId(roomId)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .content(request.getContent())
                .build();

        scheduleMapper.update(schedule);
        return toScheduleResponse(schedule);
    }

    public void deleteSchedule(Long roomId, Long scheduleId) {
        scheduleMapper.delete(scheduleId);
    }

    private ScheduleResponse.Schedule toScheduleResponse(Schedule schedule) {
        return ScheduleResponse.Schedule.builder()
                .scheduleId(schedule.getScheduleId())
                .roomId(schedule.getRoomId())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .content(schedule.getContent())
                .build();
    }
}