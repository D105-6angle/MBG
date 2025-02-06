package com.ssafy.controller.schedule;


import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ScheduleRequest {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String content;
}
