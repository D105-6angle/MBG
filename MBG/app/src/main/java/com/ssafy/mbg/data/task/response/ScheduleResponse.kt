package com.ssafy.mbg.data.task.response

import com.ssafy.mbg.data.task.dao.Schedule

data class ScheduleResponse(
    val schedules : List<Schedule>
)