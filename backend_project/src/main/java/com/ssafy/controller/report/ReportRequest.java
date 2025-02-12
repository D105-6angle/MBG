package com.ssafy.controller.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReportRequest {
    private Long roomId;
    private Long userId;
    private int no1;
    private int no2;
    private int no3;
    private String no4;
}
