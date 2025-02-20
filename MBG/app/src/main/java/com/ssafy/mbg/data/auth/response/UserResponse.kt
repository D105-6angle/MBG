package com.ssafy.mbg.data.auth.response

import com.ssafy.mbg.data.auth.dao.AttemptedProblem
import com.ssafy.mbg.data.auth.dao.UserProfile

data class UserResponse(
    val userInfo : UserProfile,
    val attemptedProblems : List<AttemptedProblem>
)