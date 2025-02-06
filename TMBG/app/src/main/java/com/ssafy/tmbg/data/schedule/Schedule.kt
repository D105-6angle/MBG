package com.ssafy.tmbg.data.schedule

import java.util.*

data class Schedule(
    val schedulesId: Long,
    val roomId: Long,
    val startTime: Date,
    val endTime: Date,
    val content: String
) {
    // 서버 통신을 위한 생성자나 변환 함수가 필요하다면 여기에 추가
    companion object {
        fun createSchedule(
            schedulesId: Long,
            roomId: Long,
            startTime: Date,
            endTime: Date,
            content: String
        ): Schedule {
            return Schedule(
                schedulesId = schedulesId,
                roomId = roomId,
                startTime = startTime,
                endTime = endTime,
                content = content
            )
        }
    }
}

