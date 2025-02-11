package com.ssafy.mbg.data.mypage.repository

import com.ssafy.mbg.api.MyPageApi
import com.ssafy.mbg.data.auth.response.UserResponse
import com.ssafy.mbg.data.mypage.response.ProblemResponse
import retrofit2.Response
import javax.inject.Inject


class MyPageRepositoryImpl @Inject constructor(
    private val myPageApi: MyPageApi
) : MyPageRepository {

    override suspend fun getUserInfo(userId: Long): Response<UserResponse> {
        return myPageApi.getUserInfo(userId)
    }

    override suspend fun getDetailProblem(userId: Long, logId: String): Response<ProblemResponse> {
        return myPageApi.getDetailProblemHistory(userId, logId)
    }
}