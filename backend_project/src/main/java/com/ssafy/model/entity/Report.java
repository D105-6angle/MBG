package com.ssafy.model.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {
    private Long roomId;
    private Long userId;
    private String no1;
    private String no2;
    private String no3;
    private String no4;
}
