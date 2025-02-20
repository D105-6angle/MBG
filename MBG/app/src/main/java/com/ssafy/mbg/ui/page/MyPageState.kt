package com.ssafy.mbg.ui.page

import com.ssafy.mbg.data.auth.response.UserResponse
import com.ssafy.mbg.data.mypage.response.ProblemResponse
import com.ssafy.mbg.data.mypage.response.ProfileResponse

sealed class MyPageState {
    data object Initial : MyPageState()
    data object Loading : MyPageState()
    data class Success(
        val profileResponse: ProfileResponse? = null,
        val problemResponse: ProblemResponse? = null
    ) : MyPageState()
    data class Error(val message: String) : MyPageState()
}