package com.ssafy.tmbg.data.profile.response

import com.ssafy.tmbg.data.profile.dao.AttemptedProblem
import com.ssafy.tmbg.data.profile.dao.UserInfo

data class ProfileResponse(
    val userInfo : UserInfo,
    val attemptedProblesm : List<AttemptedProblem>
)
