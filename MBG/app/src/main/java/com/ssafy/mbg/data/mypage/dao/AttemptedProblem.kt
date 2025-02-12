package com.ssafy.mbg.data.mypage.dao

data class AttemptedProblem(
    val cardId: Long,
    val name : String,
    val imageUrl : String,
    val lastAttempedAt : String
)