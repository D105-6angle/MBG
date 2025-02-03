package com.ssafy.mbg.data

data class Notification (
    val notificationId : Int? = null,
    // 받는 기기의 fcm 토큰
    val tokenId : Int? = null,
    val title : String,
    val body : String,
    val room : String? = null,
    val createAt : String
)
