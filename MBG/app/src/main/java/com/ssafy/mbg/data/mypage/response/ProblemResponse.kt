package com.ssafy.mbg.data.mypage.response

data class ProblemResponse(
    val lastAttemptedAt : String,
    val cardName : String,
    val imageUrl : String,
    val description : String
)