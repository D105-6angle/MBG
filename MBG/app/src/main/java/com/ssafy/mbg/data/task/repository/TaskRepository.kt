package com.ssafy.mbg.data.task.repository

import com.ssafy.mbg.data.task.response.MyTeamResponse
import com.ssafy.mbg.data.task.response.ScheduleResponse

interface TaskRepository {
    suspend fun getSchedules(roomId : Long) : Result<ScheduleResponse>
    suspend fun getMyTeams(roomId: Long, groupNo : Long) : Result<MyTeamResponse>
}