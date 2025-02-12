package com.ssafy.tmbg.api

import com.ssafy.tmbg.data.auth.request.LoginRequest
import com.ssafy.tmbg.data.auth.request.RegisterRequest
import com.ssafy.tmbg.data.auth.request.UpdateUserRequest
import com.ssafy.tmbg.data.auth.response.LoginResponse
import com.ssafy.tmbg.data.auth.response.RegisterResponse

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApi {
    /** 소셜 로그인 정보로 서버 인증 요청 */
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ) : Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ) : Response<RegisterResponse>

    @PATCH("mypage/{userId}/nickname")
    suspend fun updateUserNickname(
        @Path("userId") userId: Long,
        @Body request: UpdateUserRequest
    ) : Response<Unit>

    // 회원 탈퇴
    @DELETE("mypage/{userId}")
    suspend fun withDraw(
        @Path("userId") userId: Long
    ): Response<Unit>

}