package com.ssafy.tmbg.data.report.repositoy

import com.ssafy.tmbg.data.report.response.ReportResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ReportRepository {
    suspend fun getReport(roomId: Int) : Result<ReportResponse>
}