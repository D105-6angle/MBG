package com.ssafy.tmbg.data.schedule.dao

import java.util.*
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ScheduleResponse(
    val schedules: List<Schedule>
) 