package com.ssafy.tmbg.data.report.response

import com.ssafy.tmbg.data.report.dao.ReportData
import com.ssafy.tmbg.data.report.dao.Student

data class ReportResponse(
    val reports: Report
)

data class Report(
    val roomName: String,
    val completed: Boolean,
    val students: List<Student>,
    val reportData: ReportData
)