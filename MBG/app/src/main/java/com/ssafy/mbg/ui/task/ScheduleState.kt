package com.ssafy.mbg.ui.task

import com.ssafy.mbg.data.task.dao.Schedule


sealed class ScheduleState {
    data object Initial : ScheduleState()
    data object Loading : ScheduleState()
    data class Error(val message: String) : ScheduleState()
    data class Success(
        val schedules: List<Schedule> // 변경: 단일 일정 대신 리스트로 변경
    ) : ScheduleState()
}