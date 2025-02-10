package com.ssafy.mbg.data.task.dao

data class ScheduleRequest(
    val roomId : Long,
    val startTime : String,
    val endTime : String,
    val content : String
)