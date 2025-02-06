package com.ssafy.controller.schedule;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ScheduleListResponse {
    private List<ScheduleResponse> schedules;
}
