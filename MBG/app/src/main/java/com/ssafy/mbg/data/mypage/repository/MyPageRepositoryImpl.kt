package com.ssafy.mbg.data.mypage.repository

import com.ssafy.mbg.api.MyPageApi
import com.ssafy.mbg.data.mypage.response.ProblemResponse
import com.ssafy.mbg.data.mypage.response.ProfileResponse
import retrofit2.Response
import javax.inject.Inject


class MyPageRepositoryImpl @Inject constructor(
    private val myPageApi: MyPageApi
) : MyPageRepository {

    override suspend fun getProfile(userId: Long, roomId : Long?): Response<ProfileResponse> {
        return myPageApi.getProfile(userId, roomId)
    }

    override suspend fun getDetailProblem(userId: Long, cardId: Long): Response<ProblemResponse> {
        return myPageApi.getDetailProblemHistory(userId, cardId)
    }
}