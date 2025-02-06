package com.ssafy.mbg.data.auth.repository

import com.ssafy.mbg.data.auth.dto.LoginRequest
import com.ssafy.mbg.data.auth.dto.LoginResponse
import com.ssafy.mbg.data.auth.dto.RegisterRequest
import com.ssafy.mbg.data.auth.dto.RegisterResponse
import com.ssafy.mbg.data.mypage.dto.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ) : Response<ApiResponse<LoginResponse>>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ) : Response<ApiResponse<RegisterResponse>>
}