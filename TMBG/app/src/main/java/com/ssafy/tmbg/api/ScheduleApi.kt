package com.ssafy.tmbg.api

import com.ssafy.tmbg.data.schedule.dao.Schedule
import com.ssafy.tmbg.data.schedule.dao.ScheduleRequest
import com.ssafy.tmbg.data.schedule.dao.ScheduleResponse
import com.ssafy.tmbg.data.schedule.dao.ScheduleUpdateRequest
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Body

/**
 * 일정 관련 API 요청을 정의하는 인터페이스
 * Retrofit을 사용하여 서버와 통신합니다.
 */
interface ScheduleApi {
    /**
     * 특정 방의 모든 일정을 조회합니다.
     * @param roomId 방 ID
     * @return 해당 방의 모든 일정 목록을 포함한 Response
     * HTTP Method: GET
     * Endpoint: rooms/{roomId}/schedules
     */
    @GET("rooms/{roomId}/schedules")
    suspend fun getSchedules(@Path("roomId") roomId: Int): Response<ScheduleResponse>

    /**
     * 새로운 일정을 생성합니다.
     * @param roomId 방 ID
     * @param scheduleRequest 생성할 일정 정보 (ID 제외)
     * @return 생성된 일정 정보를 포함한 Response (서버에서 생성된 ID 포함)
     * HTTP Method: POST
     * Endpoint: rooms/{roomId}/schedules
     */
    @POST("rooms/{roomId}/schedules")
    suspend fun createSchedule(
        @Path("roomId") roomId: Int,
        @Body scheduleRequest: ScheduleRequest
    ): Response<Schedule>

    /**
     * 기존 일정을 수정합니다.
     * @param roomId 방 ID
     * @param scheduleId 수정할 일정 ID
     * @param scheduleUpdateRequest 수정할 일정 정보
     * @return 수정된 일정 정보를 포함한 Response
     * HTTP Method: PUT
     * Endpoint: rooms/{roomId}/schedules/{scheduleId}
     */
    @PUT("rooms/{roomId}/schedules/{scheduleId}")
    suspend fun updateSchedule(
        @Path("roomId") roomId: Int,
        @Path("scheduleId") scheduleId: Int,
        @Body scheduleUpdateRequest: ScheduleUpdateRequest
    ): Response<Schedule>

    /**
     * 일정을 삭제합니다.
     * @param roomId 방 ID
     * @param scheduleId 삭제할 일정 ID
     * @return 삭제 결과를 포함한 Response
     * HTTP Method: DELETE
     * Endpoint: rooms/{roomId}/schedules/{scheduleId}
     */
    @DELETE("rooms/{roomId}/schedules/{scheduleId}")
    suspend fun deleteSchedule(
        @Path("roomId") roomId: Int,
        @Path("scheduleId") scheduleId: Int
    ): Response<Unit>
} 