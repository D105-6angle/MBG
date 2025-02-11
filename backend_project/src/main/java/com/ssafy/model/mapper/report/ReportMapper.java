package com.ssafy.model.mapper.report;

import com.ssafy.controller.report.StudentDto;
import com.ssafy.model.entity.Report;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReportMapper {
    String findRoomName(Long roomId);
    List<StudentDto> findAllStudentsByRoomId(Long roomId);
    List<StudentDto> findSubmittedStudentsByRoomId(Long roomId);

    List<Report> findAllReportsByRoomId(Long roomId);
}
