package com.ssafy.schedules.model;

import java.time.LocalDateTime;

import com.ssafy.group.model.Room;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {
    private Long scheduleId;
    private Long roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String content;
    // 연관
    private Room room;
}