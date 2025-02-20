package com.ssafy.mbg.data.auth.dao

data class AttemptedProblem(
    val logId : String,
    val cardId : String,
    val name : String,
    val imageUrl : String,
    val lastAttempedAt : String
)
