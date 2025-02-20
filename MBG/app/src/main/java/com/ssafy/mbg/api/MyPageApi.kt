package com.ssafy.mbg.api

import com.ssafy.mbg.data.auth.response.UserResponse
import com.ssafy.mbg.data.mypage.dao.UpdateNicknameRequest
import com.ssafy.mbg.data.mypage.response.ProblemResponse
import com.ssafy.mbg.data.mypage.response.ProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface MyPageApi {
    // 마이 페이지 전체 데이터 조회
    @GET("mypage/{userId}")
    suspend fun getProfile(
        @Path("userId") userId: Long,
        @Query("roomId") roomId : Long?
        ): Response<ProfileResponse>

    // 내가 푼 문제 상세 조회
    @GET("mypage/{userId}/card/{cardId}")
    suspend fun getDetailProblemHistory(
        @Path("userId") userId: Long,
        @Path("cardId") cardId: Long,
    ): Response<ProblemResponse>
    // 닉네임 변경

    @PATCH("mypage/{userId}/nickname")
    suspend fun updateUserNickname(
        @Path("userId") userId: Long,
        @Body request: UpdateNicknameRequest
    ) : Response<Unit>

    // 회원 탈퇴
    @DELETE("mypage/{userId}")
    suspend fun withDraw(
        @Path("userId") userId: Long
    ): Response<Unit>

}