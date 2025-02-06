package com.ssafy.model.entity;

import java.time.LocalDateTime;

import com.ssafy.model.entity.Room;
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