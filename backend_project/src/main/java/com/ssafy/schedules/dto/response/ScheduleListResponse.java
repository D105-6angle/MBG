package com.ssafy.schedules.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Builder
public class ScheduleListResponse {
    private List<ScheduleResponse> schedules;
}
