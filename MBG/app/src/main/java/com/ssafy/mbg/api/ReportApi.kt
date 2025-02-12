package com.ssafy.mbg.api

import android.util.Log
import com.ssafy.mbg.data.report.request.ReportRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportApi {
    @POST("reports")
    suspend fun createReport(
        @Body request: ReportRequest
    ): Response<Unit>
}
