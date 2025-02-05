package com.ssafy.schedules.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ScheduleResponse {
    private Long scheduleId;
    private Long roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String content;
}
