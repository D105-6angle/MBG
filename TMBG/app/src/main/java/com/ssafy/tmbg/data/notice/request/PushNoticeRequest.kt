package com.ssafy.tmbg.data.notice.request

data class PushNoticeRequest(
    val validate_only : Boolean,
    val message: Message
)

data class Message(
    val token : String,
    val notification : Notification
)

data class Notification(
    val title : String,
    val body : String,
)