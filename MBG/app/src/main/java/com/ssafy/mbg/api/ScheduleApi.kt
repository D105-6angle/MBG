package com.ssafy.mbg.api

import com.ssafy.mbg.data.task.response.MyTeamResponse
import com.ssafy.mbg.data.task.response.ScheduleResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ScheduleApi {
    @GET("rooms/{roomId}/schedules")
    suspend fun getSchedules(
        @Path("roomId")
        roomId : Long
    ): Response<ScheduleResponse>

    @GET("rooms/{roomId}/groups/{groupNo}")
    suspend fun getMyTeams(
        @Path("roomId") roomId: Long,
        @Path("groupNo") groupNo : Long,
    ) : Response<MyTeamResponse>
}