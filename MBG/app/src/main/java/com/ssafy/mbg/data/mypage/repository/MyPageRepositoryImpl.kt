package com.ssafy.mbg.data.mypage.repository

import com.ssafy.mbg.api.MyPageApi
import com.ssafy.mbg.data.auth.dao.UserProfile
import com.ssafy.mbg.data.auth.common.ApiResponse
import com.ssafy.mbg.data.mypage.dto.ProblemHistory
import com.ssafy.mbg.data.mypage.dto.UserInfo
import retrofit2.Response

class MyPageRepository(
    private val myPageApi: MyPageApi
) {


    // User 정보 가져 오기
    suspend fun getUserInfo(userId: String) : Response<ApiResponse<UserInfo>> {
        return myPageApi.getUserInfo(userId)
    }

    // Profile 정보 가져 오기
    suspend fun getProfile(userId: String) : Response<ApiResponse<UserProfile>> {
        return myPageApi.getUserSetting(userId)
    }

    // User 가 푼 문제 기록 상세
    suspend fun getDetailProblem(userId: String, logId : String) : Response<ApiResponse<ProblemHistory>> {
        return myPageApi.getDetailProblemHistory(userId, logId)
    }

}