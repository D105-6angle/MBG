package com.ssafy.tmbg.data.report.repositoy

import com.ssafy.tmbg.data.report.response.ReportResponse

interface ReportRepository {
    suspend fun getReport(roomId: Int) : Result<ReportResponse>
}