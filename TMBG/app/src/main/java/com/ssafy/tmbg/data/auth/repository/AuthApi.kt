package com.ssafy.tmbg.data.auth.repository

import com.ssafy.tmbg.data.auth.dto.LoginRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    /** 소셜 로그인 정보로 서버 인증 요청 */
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    )
}