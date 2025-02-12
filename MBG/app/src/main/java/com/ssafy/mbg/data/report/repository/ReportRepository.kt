package com.ssafy.mbg.data.report.repository

import com.ssafy.mbg.data.report.request.ReportRequest
import retrofit2.Response

interface ReportRepository {
    suspend fun createReport(request: ReportRequest) : Result<Unit>

}