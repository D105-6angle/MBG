package com.ssafy.model.service.report;

import com.ssafy.controller.report.ReportRequest;
import com.ssafy.controller.report.ReportResponse;
import com.ssafy.controller.report.StudentDto;
import com.ssafy.exception.report.DuplicateReportException;
import com.ssafy.exception.schedule.RoomNotFoundException;
import com.ssafy.model.entity.Report;
import com.ssafy.model.mapper.report.ReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {
    private final ReportMapper reportMapper;

    public ReportResponse getReportsByRoomId(Long roomId) {
        String roomName = reportMapper.findRoomName(roomId);
        if (roomName == null) {
            throw new RuntimeException("Room not found");
        }
        List<StudentDto> allStudents = reportMapper.findAllStudentsByRoomId(roomId);
        List<StudentDto> submittedStudents = reportMapper.findSubmittedStudentsByRoomId(roomId);
        List<Report> reports = reportMapper.findAllReportsByRoomId(roomId);

        boolean isCompleted = allStudents.size() == submittedStudents.size();

        if (!isCompleted) {
            return ReportResponse.builder()
                    .reports(ReportResponse.Reports.builder()
                            .roomName(roomName)
                            .isCompleted(false)
                            .students(submittedStudents)
                            .reportData(ReportResponse.ReportDataDto.builder()
                                    .satisfaction(Collections.emptyList())
                                    .comments(Collections.emptyList())
                                    .build())
                            .build())
                    .build();
        }

        return ReportResponse.builder()
                .reports(ReportResponse.Reports.builder()
                        .roomName(roomName)
                        .isCompleted(true)
                        .students(submittedStudents)
                        .reportData(ReportResponse.ReportDataDto.builder()
                                .satisfaction(calculateSatisfactionRates(reports))
                                .comments(extractComments(reports))
                                .build())
                        .build())
                .build();
    }

    private List<ReportResponse.SatisfactionDto> calculateSatisfactionRates(List<Report> reports) {
        if (reports.isEmpty()) {
            return Arrays.asList(
                    createEmptySatisfactionDto(),
                    createEmptySatisfactionDto(),
                    createEmptySatisfactionDto()
            );
        }

        return Arrays.asList(
                calculateQuestionSatisfaction(reports, Report::getNo1),
                calculateQuestionSatisfaction(reports, Report::getNo2),
                calculateQuestionSatisfaction(reports, Report::getNo3)
        );
    }

    private ReportResponse.SatisfactionDto calculateQuestionSatisfaction(
            List<Report> reports,
            Function<Report, Integer> questionGetter  // String -> Integer
    ) {
        int totalResponses = reports.size();

        Map<Integer, Long> ratingCounts = reports.stream()
                .map(questionGetter)
                .collect(Collectors.groupingBy(
                        rating -> rating,
                        Collectors.counting()
                ));

        return ReportResponse.SatisfactionDto.builder()
                .veryGood_rate(calculateRate(ratingCounts.getOrDefault(5, 0L), totalResponses))
                .good_rate(calculateRate(ratingCounts.getOrDefault(4, 0L), totalResponses))
                .neutral_rate(calculateRate(ratingCounts.getOrDefault(3, 0L), totalResponses))
                .bad_rate(calculateRate(ratingCounts.getOrDefault(2, 0L), totalResponses))
                .veryBad_rate(calculateRate(ratingCounts.getOrDefault(1, 0L), totalResponses))
                .build();
    }

    private double calculateRate(long count, int total) {
        return ((double) count / total);
    }

    private ReportResponse.SatisfactionDto createEmptySatisfactionDto() {
        return ReportResponse.SatisfactionDto.builder()
                .veryGood_rate(0)
                .good_rate(0)
                .neutral_rate(0)
                .bad_rate(0)
                .veryBad_rate(0)
                .build();
    }

    private List<String> extractComments(List<Report> reports) {
        return reports.stream()
                .map(Report::getNo4)
                .filter(comment -> comment != null && !comment.trim().isEmpty())
                .collect(Collectors.toList());
    }

    @Transactional
    public void submitReport(ReportRequest request) {
        if (!reportMapper.existsRoom(request.getRoomId())) {
            throw new RoomNotFoundException("존재하지 않는 방입니다.");
        }

        Report existingReport = reportMapper.getReport(request.getRoomId(), request.getUserId());
        if (existingReport != null) {
            throw new DuplicateReportException("이미 설문을 제출하셨습니다.");
        }

        Report report = Report.builder()
                .roomId(request.getRoomId())
                .userId(request.getUserId())
                .no1(request.getNo1())
                .no2(request.getNo2())
                .no3(request.getNo3())
                .no4(request.getNo4())
                .build();

        reportMapper.insertReport(report);
    }

    public Report getReport(Long roomId, Long userId) {
        return reportMapper.getReport(roomId, userId);
    }

}