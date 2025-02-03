package com.ssafy.schedules.model;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {
    private Long schedulesId;
    private Long roomId;
    private LocalDateTime time;
    private String content;
}