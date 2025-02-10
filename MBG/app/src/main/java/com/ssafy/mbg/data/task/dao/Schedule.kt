package com.ssafy.mbg.data.task.dao

import java.util.Calendar
import java.util.Date

data class Schedule(
    val schedulesId: Number,
    val roomId: Number,
    val startTime: String,
    val endTime: String,
    val content: String
) {
    fun getStatTimeAsDate(): Date {
        return parseTimeString(startTime)
    }

    fun getEndTimeAsData(): Date {
        return parseTimeString(endTime)
    }

    private fun parseTimeString(timeString: String): Date {
        val (hour, minute) = timeString.split(
            ":"
        ).map { it.toInt() }
        return  Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }.time
    }
}