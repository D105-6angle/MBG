package com.ssafy.mbg.data.mypage.repository

import com.ssafy.mbg.data.auth.dto.UserProfile
import com.ssafy.mbg.data.mypage.dto.ApiResponse
import com.ssafy.mbg.data.mypage.dto.ProblemHistory
import com.ssafy.mbg.data.mypage.dto.UpdateNicknameRequest
import com.ssafy.mbg.data.mypage.dto.UserInfo
import com.ssafy.mbg.network.ApiClient
import retrofit2.Response

class MyPageRepository {
    private val myPageApi: MyPageApi = ApiClient.retrofit.create(MyPageApi::class.java)

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

    // 닉네임 변경
    suspend fun updateNickname(userId: String, newNickname: String) : Response<ApiResponse<Unit>> {
        return myPageApi.patchUserNickname(
            userId = userId,
            request = UpdateNicknameRequest(nickname = newNickname)
        )
    }

    // 회원 탈퇴
    suspend fun deleteUser(userId: String) : Response<ApiResponse<Unit>> {
        return myPageApi.deleteUser(userId)
    }
}