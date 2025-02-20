package com.ssafy.tmbg.data.notice.dao

data class Notice(
    val noticeId : Long,
    val roomId : Long,
    val title : String,
    val content : String,
    val createdAt : String
)
