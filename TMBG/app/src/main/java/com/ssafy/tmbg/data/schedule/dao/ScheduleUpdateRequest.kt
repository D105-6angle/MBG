package com.ssafy.tmbg.data.schedule.dao

/**
 * 일정 수정 요청 시 사용되는 데이터 클래스
 * 
 * @property startTime 수정할 일정 시작 시간
 * @property endTime 수정할 일정 종료 시간
 * @property content 수정할 일정 내용
 */
data class ScheduleUpdateRequest(
    val startTime: String,
    val endTime: String,
    val content: String
) 