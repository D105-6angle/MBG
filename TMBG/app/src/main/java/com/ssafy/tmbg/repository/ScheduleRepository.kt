package com.ssafy.tmbg.repository

import com.ssafy.tmbg.api.ScheduleApi
import javax.inject.Inject

class ScheduleRepository @Inject constructor(
    private val scheduleApi: ScheduleApi
) {
    suspend fun getSchedules(roomId: Long) = scheduleApi.getSchedules(roomId)
} 