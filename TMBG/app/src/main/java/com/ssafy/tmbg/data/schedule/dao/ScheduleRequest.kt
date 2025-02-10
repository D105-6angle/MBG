package com.ssafy.tmbg.data.schedule.dao

import java.util.*

/**
 * 일정 생성 요청 시 사용되는 데이터 클래스
 * ID가 없는 일정 정보를 서버에 전송할 때 사용됩니다.
 *
 * @property roomId 일정을 생성할 방의 ID
 * @property startTime 일정 시작 시간
 * @property endTime 일정 종료 시간
 * @property content 일정 내용
 */
data class ScheduleRequest(
    val roomId: Int,
    val startTime: String,
    val endTime: String,
    val content: String
) 