package com.ssafy.tmbg.api

import com.ssafy.tmbg.data.report.response.ReportResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ReportApi {
    @GET("reports/{roomId}")
    suspend fun getReport(
        @Path("roomId") roomId: Int
    ): Response<ReportResponse>
}