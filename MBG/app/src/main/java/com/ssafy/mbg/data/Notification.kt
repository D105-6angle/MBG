package com.ssafy.mbg.data

import com.google.gson.annotations.SerializedName

/**
 * 알림(또는 알람) 데이터를 위한 모델 클래스
 * GET /api/alarm/{userId} 응답 JSON과 매핑되도록 @SerializedName을 사용합니다.
 */
data class Notification (
    @SerializedName("alarmId")
    val notificationId : Int? = null,
    @SerializedName("userId")
    val userId: Int? = null,
    val title : String,
    @SerializedName("content")
    val body : String,
    val room : String? = null,
    @SerializedName("sentTime")
    val createAt : String
)
