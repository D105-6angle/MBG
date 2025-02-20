package com.ssafy.tmbg.data.schedule.repository

import com.ssafy.tmbg.api.ScheduleApi
import com.ssafy.tmbg.data.schedule.dao.Schedule
import com.ssafy.tmbg.data.schedule.dao.ScheduleRequest
import javax.inject.Inject
import retrofit2.Response
import com.ssafy.tmbg.data.schedule.dao.ScheduleResponse
import com.ssafy.tmbg.data.schedule.dao.ScheduleUpdateRequest

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
    suspend fun getSchedules(roomId: Int): Response<ScheduleResponse> = scheduleApi.getSchedules(roomId)
    
    /**
     * 새 일정을 생성합니다.
     * @param roomId 방 ID
     * @param scheduleRequest 생성할 일정 정보
     * @return API 응답
     */
    suspend fun createSchedule(roomId: Int, scheduleRequest: ScheduleRequest): Response<Schedule> = 
        scheduleApi.createSchedule(roomId, scheduleRequest)
    
    suspend fun updateSchedule(roomId: Int, scheduleId: Int, scheduleUpdateRequest: ScheduleUpdateRequest): Response<Schedule> =
        scheduleApi.updateSchedule(roomId, scheduleId, scheduleUpdateRequest)
    
    suspend fun deleteSchedule(roomId: Int, scheduleId: Int): Response<Unit> = 
        scheduleApi.deleteSchedule(roomId, scheduleId)
} 