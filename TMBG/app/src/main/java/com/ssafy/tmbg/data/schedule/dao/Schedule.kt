package com.ssafy.tmbg.data.schedule.dao

import java.util.*

/**
 * 일정 정보를 담는 데이터 클래스
 * 서버와의 통신 및 앱 내부에서 사용됩니다.
 *
 * @property scheduleId 일정의 고유 ID
 * @property roomId 일정이 속한 방의 ID
 * @property startTime 일정 시작 시간
 * @property endTime 일정 종료 시간
 * @property content 일정 내용
 */
data class Schedule(
    val scheduleId: Int,
    val roomId: Int,
    val startTime: String,
    val endTime: String,
    val content: String
)

