package com.ssafy.schedules.service;


import com.ssafy.schedules.dto.request.ScheduleRequest;
import com.ssafy.schedules.dto.response.ScheduleListResponse;
import com.ssafy.schedules.dto.response.ScheduleResponse;
import com.ssafy.schedules.mapper.ScheduleMapper;
import com.ssafy.schedules.model.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleMapper scheduleMapper;

    public ScheduleListResponse getSchedulesByRoomId(Long roomId) {
        List<Schedule> schedules = scheduleMapper.findByRoomId(roomId);
        List<ScheduleResponse> responses = schedules.stream()
                .map(this::toScheduleResponse)
                .collect(Collectors.toList());

        return ScheduleListResponse.builder()
                .schedules(responses)
                .build();
    }

    public ScheduleResponse getSchedule(Long roomId, Long scheduleId) {
        Schedule schedule = scheduleMapper.findByRoomIdAndScheduleId(roomId, scheduleId);
        return toScheduleResponse(schedule);
    }

    public ScheduleResponse createSchedule(Long roomId, ScheduleRequest request) {
        Schedule schedule = Schedule.builder()
                .roomId(roomId)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .content(request.getContent())
                .build();

        scheduleMapper.insert(schedule);
        return toScheduleResponse(schedule);
    }

    public ScheduleResponse updateSchedule(Long roomId, Long scheduleId, ScheduleRequest request) {
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

    private ScheduleResponse toScheduleResponse(Schedule schedule) {
        return ScheduleResponse.builder()
                .scheduleId(schedule.getScheduleId())
                .roomId(schedule.getRoomId())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .content(schedule.getContent())
                .build();
    }
}