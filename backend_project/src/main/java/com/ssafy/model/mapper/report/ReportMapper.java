package com.ssafy.model.mapper.report;

import com.ssafy.controller.report.StudentDto;
import com.ssafy.model.entity.Report;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {
    String findRoomName(Long roomId);
    List<StudentDto> findAllStudentsByRoomId(Long roomId);
    List<StudentDto> findSubmittedStudentsByRoomId(Long roomId);
    List<Report> findAllReportsByRoomId(Long roomId);
    void insertReport(Report report);
    Report getReport(@Param("roomId") Long roomId, @Param("userId") Long userId);
    boolean existsRoom(Long roomId);

}