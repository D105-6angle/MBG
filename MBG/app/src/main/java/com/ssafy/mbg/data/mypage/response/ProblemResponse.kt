package com.ssafy.mbg.data.mypage.response

data class ProblemResponse(
    val logId : Long,
    val cardId : Long,
    val lastAttemptedAt : String,
    val cardName : String,
    val imageUrl : String,
    val description : String
)