package com.ssafy.tmbg.data.profile.dao

data class AttemptedProblem(
    val cardId: Long,
    val name : String,
    val imageUrl : String,
    val lastAttempedAt : String
)
