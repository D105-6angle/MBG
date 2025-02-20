package com.ssafy.tmbg.data.auth.repository

import com.ssafy.tmbg.data.auth.request.LoginRequest
import com.ssafy.tmbg.data.auth.request.RegisterRequest
import com.ssafy.tmbg.data.auth.response.LoginResponse
import com.ssafy.tmbg.data.auth.response.RegisterResponse
import com.ssafy.tmbg.data.auth.response.WithdrawResponse


// 전체 인증 처리를 위한 인터페이스
interface AuthRepository {
    suspend fun login(loginRequest: LoginRequest) : Result<LoginResponse>
    suspend fun register(registerRequest: RegisterRequest) : Result<RegisterResponse>
    suspend fun updateNickname(userId: Long, nickname : String) : Result<Unit>
    suspend fun withDraw(userId : Long) : Result<Unit>
}