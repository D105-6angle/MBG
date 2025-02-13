package com.ssafy.mbg.ui.task

import com.ssafy.mbg.data.task.dao.Schedule

sealed class ScheduleState {
    data object Initial : ScheduleState()
    data object Loading : ScheduleState()
    data class Success(val schedules: List<Schedule>) : ScheduleState()
    data class Error(val message: String) : ScheduleState()
}