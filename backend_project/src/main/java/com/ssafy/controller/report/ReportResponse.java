package com.ssafy.controller.report;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class ReportResponse {
    private String roomName;
    private boolean isCompleted;
    private List<StudentDto> students;
    private ReportDataDto reportData;

    @Getter
    @Builder
    public static class StudentDto {
        private String name;
    }

    @Getter
    @Builder
    public static class ReportDataDto {
        private List<SatisfactionDto> satisfaction;
        private List<String> comments;
    }

    @Getter
    @Builder
    public static class SatisfactionDto {
        private double veryGood_rate;
        private double good_rate;
        private double neutral_rate;
        private double bad_rate;
        private double veryBad_rate;
    }
}
