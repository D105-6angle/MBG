package com.ssafy.tmbg.api

import com.ssafy.tmbg.data.auth.request.LoginRequest
import com.ssafy.tmbg.data.auth.request.RegisterRequest
import com.ssafy.tmbg.data.auth.response.LoginResponse
import com.ssafy.tmbg.data.auth.response.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

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
}