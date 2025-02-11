package com.ssafy.tmbg.data.report.response

import com.ssafy.tmbg.data.report.dao.ReportData
import com.ssafy.tmbg.data.report.dao.Student

data class ReportResponse(
    val roomName : String,
    val isCompleted: Boolean,
    val students : List<Student>,
    val reportData : ReportData
)
