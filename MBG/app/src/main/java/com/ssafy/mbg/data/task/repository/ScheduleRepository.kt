package com.ssafy.mbg.data.task.repository

import com.ssafy.mbg.api.ScheduleApi
import com.ssafy.mbg.data.task.dao.ScheduleResponse
import retrofit2.Response
import javax.inject.Inject

class ScheduleRepository @Inject constructor(
    private val scheduleApi: ScheduleApi
){
    /**
     * 일정 전체 조회
     */
    suspend fun getSchedules(roomId: Long): Response<ScheduleResponse> = scheduleApi.getSchedules(roomId)

}