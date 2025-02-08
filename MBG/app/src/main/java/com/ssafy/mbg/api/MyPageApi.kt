package com.ssafy.mbg.api

import com.ssafy.mbg.data.auth.dto.UserProfile
import com.ssafy.mbg.data.auth.common.ApiResponse
import com.ssafy.mbg.data.auth.dto.WithdrawResponse
import com.ssafy.mbg.data.mypage.dto.ProblemHistory
import com.ssafy.mbg.data.mypage.dto.UpdateNicknameRequest
import com.ssafy.mbg.data.mypage.dto.UserInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface MyPageApi {
    // 마이 페이지 전체 데이터 조회
    @GET("users/{userId}/myPage")
    suspend fun getUserInfo(@Path("userId") userId: String): Response<ApiResponse<UserInfo>>

    // 내가 푼 문제 상세 조회
    @GET("users/{userId}/log/{logId}")
    suspend fun getDetailProblemHistory(
        @Path("userId") userId: String,
        @Path("logId") logId: String,
    ): Response<ApiResponse<ProblemHistory>>

    // 프로필 정보 가져오기
    @GET("users/{userId}/setting")
    suspend fun getUserSetting(
        @Path("userId") userId: String
    ) : Response<ApiResponse<UserProfile>>

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
    ): Response<WithdrawResponse>

}