package com.ssafy.mbg.api

import com.ssafy.mbg.data.auth.request.LoginRequest
import com.ssafy.mbg.data.auth.response.LoginResponse
import com.ssafy.mbg.data.auth.request.RegisterRequest
import com.ssafy.mbg.data.auth.response.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ) : Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ) : Response<RegisterResponse>

}