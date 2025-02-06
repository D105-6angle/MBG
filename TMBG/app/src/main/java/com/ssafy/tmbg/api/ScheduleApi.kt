package com.ssafy.tmbg.api

import com.ssafy.tmbg.data.schedule.Schedule
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ScheduleApi {
    ///roomid -> 방 만들때 받아오는 방 id
    // 계속 프론트가 들고있음
    @GET("api/rooms/{roomId}/schedules")
    suspend fun getSchedules(
        @Path("roomId") roomId: Long
    ): Response<List<Schedule>>
} 