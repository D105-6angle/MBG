package com.ssafy.tmbg.data.schedule.repository

import com.ssafy.tmbg.api.ScheduleApi
import com.ssafy.tmbg.data.schedule.dao.Schedule
import com.ssafy.tmbg.data.schedule.dao.ScheduleRequest
import javax.inject.Inject

/**
 * 일정 관련 API 호출을 처리하는 Repository
 * ViewModel과 API 사이의 중간 계층으로 동작합니다.
 */
class ScheduleRepository @Inject constructor(
    private val scheduleApi: ScheduleApi
) {
    /**
     * 일정 목록을 조회합니다.
     * @param roomId 방 ID
     * @return API 응답
     */
    suspend fun getSchedules(roomId: Long) = scheduleApi.getSchedules(roomId)
    
    /**
     * 새 일정을 생성합니다.
     * @param roomId 방 ID
     * @param scheduleRequest 생성할 일정 정보
     * @return API 응답
     */
    suspend fun createSchedule(roomId: Long, scheduleRequest: ScheduleRequest) = 
        scheduleApi.createSchedule(roomId, scheduleRequest)
    
    suspend fun updateSchedule(roomId: Long, scheduleId: Long, schedule: Schedule) =
        scheduleApi.updateSchedule(roomId, scheduleId, schedule)
    
    suspend fun deleteSchedule(roomId: Long, scheduleId: Long) = 
        scheduleApi.deleteSchedule(roomId, scheduleId)
} 