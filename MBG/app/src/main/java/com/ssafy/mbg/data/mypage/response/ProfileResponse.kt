package com.ssafy.mbg.data.mypage.response

import com.ssafy.mbg.data.mypage.dao.AttemptedProblem
import com.ssafy.mbg.data.mypage.dao.UserInfo

data class ProfileResponse(
    val userInfo : UserInfo,
    val attemptedProblems: List<AttemptedProblem>
)



