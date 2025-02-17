package com.ssafy.tmbg.data.notice.response

data class NoticeResponse(
    val noticeId : Long,
    val roomId : Long,
    val title : String,
    val content : String,
    val createdAt : String
)
