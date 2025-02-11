package com.ssafy.mbg.ui.page

import com.ssafy.mbg.data.auth.response.UserResponse
import com.ssafy.mbg.data.mypage.response.ProblemResponse

sealed class MyPageState {
    data object Initial : MyPageState()
    data object Loading : MyPageState()
    data class Success(
        val userResponse: UserResponse? = null,
        val problemResponse: ProblemResponse? = null
    ) : MyPageState()
    data class Error(val message: String) : MyPageState()
}