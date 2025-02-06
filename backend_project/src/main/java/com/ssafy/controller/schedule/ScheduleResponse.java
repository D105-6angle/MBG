package com.ssafy.controller.schedule;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ScheduleResponse {

    @Getter
    @Builder
    public static class Schedule {
        private Long scheduleId;
        private Long roomId;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String content;
    }

    @Getter
    @Builder
    public static class ListResponse {
        private List<Schedule> schedules;
    }
}